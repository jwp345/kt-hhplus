package com.hhplus.service.booking

import com.hhplus.exception.AlreadyReservationException
import com.hhplus.common.BookingStatusCode
import com.hhplus.exception.InvalidDateException
import com.hhplus.exception.InvalidSeatIdException
import com.hhplus.controller.booking.DatesAvailableDto
import com.hhplus.controller.booking.ReservationDto
import com.hhplus.controller.booking.SeatsAvailableDto
import com.hhplus.exception.InvalidTicketException
import com.hhplus.repository.booking.BookingRepository
import org.redisson.api.RedissonClient
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Service
class BookingService (val bookingRepository: BookingRepository, val redissonClient: RedissonClient) {

    /* TODO : 캐시(예약) 이력을 어떻게 쌓을 것인가?
    * 일정 시간마다 스케줄러 돌려서 배치성으로 실행해야 하나?
    * OR 그때그때마다 비동기로 실행시킬까?
    * */

    private final val reserveLock : String = "reserve-seat-lock"
    private final val cacheReserveKey : String = "seatReservation"

    fun findDatesAvailable(seatId : Int) : DatesAvailableDto {
        checkSeatId(seatId = seatId)
        val bookingDates : List<String> = bookingRepository.findDatesBySeatId(seatId = seatId, availableCode = BookingStatusCode.AVAILABLE.code)
        val availableDates : MutableList<String> = mutableListOf()
        for(bookingDate: String in bookingDates) {
            if(!isReserved(seatId = seatId, bookingDate = bookingDate)) {
                availableDates.add(bookingDate)
            }
        }
        return DatesAvailableDto(availableDates)
    }

    fun findSeatsAvailable(date: String): SeatsAvailableDto {
        checkBookingDate(date = date)
        val seatIds : List<Int> = bookingRepository.findSeatIdByBookingDate(bookingDate = date, availableCode = BookingStatusCode.AVAILABLE.code)
        val availableSeats : MutableList<Int> = mutableListOf()
        for(seatId: Int in seatIds) {
            if(!isReserved(seatId = seatId, bookingDate = date)) {
                availableSeats.add(seatId)
            }
        }
        return SeatsAvailableDto(availableSeats)
    }

    // 의문: 적절한 락의 소유시간 및 재시도 정책
    @Retryable(value = [org.redisson.client.RedisTimeoutException::class], maxAttempts = 2, backoff = Backoff(delay = 2000))
    fun reserveSeat(seatId: Int, bookingDate: String, userId: Long): ReservationDto {
        checkSeatId(seatId = seatId)
        checkBookingDate(date = bookingDate)
        val tickets : List<Long> = bookingRepository.findIdsBySeatIdAndBookingDateAndStatus(seatId = seatId,
            bookingDate = bookingDate, availableCode = BookingStatusCode.AVAILABLE.code)
        if(tickets.isEmpty() || tickets.size > 1) { // 티켓 유효성 검사
            throw InvalidTicketException()
        }
        val key : String = "" + seatId + "_" + bookingDate
        val mapLock = redissonClient.getLock(reserveLock)
        try {
            mapLock.lock(4, TimeUnit.SECONDS)
            val cacheMap = redissonClient.getMapCache<String, Long>(cacheReserveKey)
            if (cacheMap.contains(key)) {
                throw AlreadyReservationException()
            }
            cacheMap.put(key, userId, 300, TimeUnit.SECONDS)
            return ReservationDto(
                seatId = seatId,
                bookingDate = bookingDate,
                userId = userId,
                reservedDate = LocalDateTime.now()
            )
        } finally {
            if (mapLock != null && mapLock.isLocked && mapLock.isHeldByCurrentThread) {
                mapLock.unlock()
            }
        }
    }

    private fun checkBookingDate(date: String): LocalDateTime {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        } catch (e: Exception) {
            throw InvalidDateException()
        }
    }

    private fun checkSeatId(seatId: Int) {
        if (seatId > 50 || seatId <= 0) {
            throw InvalidSeatIdException()
        }
    }

    // 락 획득 로직도 테스트 코드를 짜야할까? 짠다면 순서 보장이 되면서 동시성 테스트가 가능한가?
    @Retryable(value = [org.redisson.client.RedisTimeoutException::class], maxAttempts = 2, backoff = Backoff(delay = 2000))
    private fun isReserved(seatId: Int, bookingDate: String): Boolean {
        val key : String = "" + seatId + "_" + bookingDate
        redissonClient.getLock(reserveLock)
        val cacheMap = redissonClient.getMapCache<String, Long>(cacheReserveKey)
        return cacheMap.contains(key)
    }

}


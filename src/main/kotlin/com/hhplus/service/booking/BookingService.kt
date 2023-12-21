package com.hhplus.service.booking

import com.hhplus.exception.AlreadyReservationException
import com.hhplus.common.BookingStatusCode
import com.hhplus.component.ReserveCacheInfo
import com.hhplus.component.ReserveMapGetter
import com.hhplus.exception.InvalidDateException
import com.hhplus.exception.InvalidSeatIdException
import com.hhplus.controller.booking.DatesAvailableDto
import com.hhplus.controller.booking.ReservationDto
import com.hhplus.controller.booking.SeatsAvailableDto
import com.hhplus.exception.InvalidTicketException
import com.hhplus.model.Booking
import com.hhplus.component.ConcertInfo
import com.hhplus.component.TicketInfo
import com.hhplus.repository.booking.BookingRepository
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Service
class BookingService (val bookingRepository: BookingRepository,
 val reserveMapGetter: ReserveMapGetter) {

    /* TODO : 캐시(예약) 이력을 어떻게 쌓을 것인가?
    * 일정 시간마다 스케줄러 돌려서 배치성으로 실행해야 하나?
    * OR 그때그때마다 비동기로 실행시킬까?
    * */

    fun findDatesAvailable(seatId : Int) : DatesAvailableDto {
        checkSeatId(seatId = seatId)
        val concertDates : List<String> = bookingRepository.findDatesBySeatId(seatId = seatId, availableCode = BookingStatusCode.AVAILABLE.code)
        val availableDates : MutableList<String> = mutableListOf()
        for(concertDate: String in concertDates) {
            if(!isReserved(seatId = seatId, concertDate = concertDate)) {
                availableDates.add(concertDate)
            }
        }
        return DatesAvailableDto(availableDates)
    }

    fun findSeatsAvailable(date: String): SeatsAvailableDto {
        checkConcertDate(date = date)
        val seatIds : List<Int> = bookingRepository.findSeatIdByConcertDate(concertDate = date, availableCode = BookingStatusCode.AVAILABLE.code)
        val availableSeats : MutableList<Int> = mutableListOf()
        for(seatId: Int in seatIds) {
            if(!isReserved(seatId = seatId, concertDate = date)) {
                availableSeats.add(seatId)
            }
        }
        return SeatsAvailableDto(availableSeats)
    }


    @Retryable(value = [org.redisson.client.RedisTimeoutException::class], maxAttempts = 2, backoff = Backoff(delay = 2000))
    fun reserveSeat(seatId: Int, concertDate: String, userId: Long): ReservationDto {
        checkSeatId(seatId = seatId)
        checkConcertDate(date = concertDate)
        val tickets : List<Booking> = bookingRepository.findIdAndPricesBySeatIdAndConcertDateAndStatus(seatId = seatId,
            concertDate = concertDate, availableCode = BookingStatusCode.AVAILABLE.code)
        if(tickets.isEmpty() || tickets.size > 1) { // 티켓 유효성 검사
            throw InvalidTicketException()
        }

        val concertInfo = ConcertInfo(seatId = seatId, date = concertDate)
        val lockAndMap : ReserveCacheInfo = reserveMapGetter.getLockAndReserveMap()
        val mapLock = lockAndMap.lock
        val cacheMap = lockAndMap.mapCache
        try {
            mapLock.lock(4, TimeUnit.SECONDS)
            if (cacheMap.contains(concertInfo)) {
                throw AlreadyReservationException()
            }
            cacheMap.put(concertInfo, TicketInfo(userId = userId, price = tickets[0].price), 300, TimeUnit.SECONDS)
            return ReservationDto(
                seatId = seatId,
                concertDate = concertDate,
                userId = userId,
                reservedDate = LocalDateTime.now()
            )
        } finally {
            if (mapLock.isLocked && mapLock.isHeldByCurrentThread) {
                mapLock.unlock()
            }
        }
    }

    private fun checkConcertDate(date: String): LocalDateTime {
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

    private fun isReserved(seatId: Int, concertDate: String): Boolean {
        val cacheMap = reserveMapGetter.getLockAndReserveMap().mapCache
        return cacheMap.contains(ConcertInfo(seatId = seatId, date = concertDate))
    }

}


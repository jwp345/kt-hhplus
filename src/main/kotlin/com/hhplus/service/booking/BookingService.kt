package com.hhplus.service.booking

import com.hhplus.exception.AlreadyReservationException
import com.hhplus.common.BookingStatusCode
import com.hhplus.exception.InvalidDateException
import com.hhplus.exception.InvalidSeatIdException
import com.hhplus.controller.booking.DatesAvailableDto
import com.hhplus.controller.booking.ReservationDto
import com.hhplus.controller.booking.SeatsAvailableDto
import com.hhplus.exception.InvalidTicketException
import com.hhplus.model.Booking
import com.hhplus.repository.booking.BookingRepository
import org.redisson.api.RedissonClient
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

    fun reserveSeat(seatId: Int, bookingDate: String, userId: Long): ReservationDto {
        checkSeatId(seatId = seatId)
        checkBookingDate(date = bookingDate)
        val tickets : List<Long> = bookingRepository.findIdsBySeatIdAndBookingDateAndStatus(seatId = seatId,
            bookingDate = bookingDate, availableCode = BookingStatusCode.AVAILABLE.code)
        if(tickets.isEmpty() || tickets.size > 1) { // 티켓 유효성 검사
            throw InvalidTicketException()
        }
        val key : String = "" + seatId + "_" + bookingDate
        val cacheMap = redissonClient.getMapCache<String, Long>("seatReservation")
        if(cacheMap.contains(key)) {
            throw AlreadyReservationException()
        }
        cacheMap.put(key, userId, 300, TimeUnit.SECONDS)
        return ReservationDto(seatId = seatId, bookingDate = bookingDate, userId = userId, reservedDate = LocalDateTime.now())
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

    private fun isReserved(seatId: Int, bookingDate: String): Boolean {
        val key : String = "" + seatId + "_" + bookingDate
        val cacheMap = redissonClient.getMapCache<String, Long>("seatReservation")
        return cacheMap.contains(key)
    }

}


package com.hhplus.service.booking

import com.hhplus.common.AlreadyReservationException
import com.hhplus.common.BookingStatusCode
import com.hhplus.common.InvalidDateException
import com.hhplus.common.InvalidSeatIdException
import com.hhplus.controller.booking.DatesAvailableDto
import com.hhplus.controller.booking.ReservationDto
import com.hhplus.controller.booking.SeatsAvailableDto
import com.hhplus.repository.booking.BookingRepository
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Service
class BookingService (val bookingRepository: BookingRepository, val redissonClient: RedissonClient) {

    private val expiredBookingTime = LocalDateTime.now().minusDays(5)

    fun findDatesAvailable(seatId : Int) : DatesAvailableDto {
        checkSeatId(seatId = seatId)
        return DatesAvailableDto(bookingRepository.findDatesBySeatId(seatId = seatId, targetDate = expiredBookingTime,
            availableCode = BookingStatusCode.AVAILABLE.code, reservedCode = BookingStatusCode.RESERVED.code))
    }

    fun findSeatsAvailable(date: String): SeatsAvailableDto {
        checkBookingDate(date = date)
        return SeatsAvailableDto(bookingRepository.findSeatIdByBookingDate(bookingDate = date, targetDate = expiredBookingTime,
            availableCode = BookingStatusCode.AVAILABLE.code, reservedCode = BookingStatusCode.RESERVED.code))
    }

    fun reserveSeat(seatId: Int, bookingDate: String, userId: Long): ReservationDto {
        checkSeatId(seatId = seatId)
        checkBookingDate(date = bookingDate)
        val key : String = "" + seatId + "_" + bookingDate
        val cacheSet = redissonClient.getSetCache<String>("seatReservation")

        if(cacheSet.contains(key)) {
            throw AlreadyReservationException()
        }
        cacheSet.add(key, 300, TimeUnit.SECONDS)
        return ReservationDto(seatId = seatId, bookingDate = bookingDate)
    }

    fun checkBookingDate(date: String): LocalDateTime {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        } catch (e: Exception) {
            throw InvalidDateException()
        }
    }

    fun checkSeatId(seatId: Int) {
        if (seatId > 50 || seatId <= 0) {
            throw InvalidSeatIdException()
        }
    }

}


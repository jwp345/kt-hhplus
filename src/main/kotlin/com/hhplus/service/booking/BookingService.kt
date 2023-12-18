package com.hhplus.service.booking

import com.hhplus.common.BookingStatusCode
import com.hhplus.common.InvalidDateException
import com.hhplus.common.InvalidSeatIdException
import com.hhplus.controller.booking.DatesAvailableDto
import com.hhplus.controller.booking.ReservationDto
import com.hhplus.controller.booking.SeatsAvailableDto
import com.hhplus.repository.booking.BookingRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class BookingService (val bookingRepository: BookingRepository) {

    private val expiredBookingTime = LocalDateTime.now().minusDays(5)

    fun findDatesAvailable(seatId : Int) : DatesAvailableDto {
        if(seatId > 50 || seatId <= 0) throw InvalidSeatIdException()
        return DatesAvailableDto(bookingRepository.findDatesBySeatId(seatId = seatId, targetDate = expiredBookingTime,
            availableCode = BookingStatusCode.AVAILABLE.code, reservedCode = BookingStatusCode.RESERVED.code))
    }

    fun findSeatsAvailable(date: String): SeatsAvailableDto {
        try {
            LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        } catch (e : Exception) {
            throw InvalidDateException()
        }
        return SeatsAvailableDto(bookingRepository.findSeatIdByBookingDate(bookingDate = date, targetDate = expiredBookingTime,
            availableCode = BookingStatusCode.AVAILABLE.code, reservedCode = BookingStatusCode.RESERVED.code))
    }

//    fun reserveSeat(seatId: Int, date: String): ReservationDto {
//
//    }
}


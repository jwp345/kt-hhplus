package com.hhplus.service.booking

import com.hhplus.common.BookingStatusCode
import com.hhplus.controller.booking.DatesAvailableDto
import com.hhplus.controller.booking.SeatsAvailableDto
import com.hhplus.repository.booking.BookingRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

@Service
class BookingService (val bookingRepository: BookingRepository) {
    fun findDatesAvailable(seatId : Int) : DatesAvailableDto {
        if(seatId > 50 || seatId <= 0) throw IllegalArgumentException()
        return DatesAvailableDto(bookingRepository.findBySeatIdAndStatus(seatId, BookingStatusCode.AVAILABLE.code))
    }

    fun findSeatsAvailable(date: String): SeatsAvailableDto {
        try {
            LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        } catch (e : Exception) {
            throw IllegalArgumentException()
        }
        return SeatsAvailableDto(bookingRepository.findByAvailableDateAndStatus(date, BookingStatusCode.AVAILABLE.code))
    }
}
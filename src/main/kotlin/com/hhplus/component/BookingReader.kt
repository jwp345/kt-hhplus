package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.exception.InvalidBookingDateException
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.domain.exception.InvalidSeatIdException
import com.hhplus.presentation.booking.BookingStatusCode
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class BookingReader(val bookingRepository: BookingRepository) {

    private val log = KotlinLogging.logger("BookingReader")
    fun read(seatId : Int) : List<Booking> {
        checkSeatId(seatId = seatId)
        return bookingRepository.findBySeatIdAndStatus(seatId = seatId, availableCode = BookingStatusCode.AVAILABLE.code)
    }

    fun read(bookingDate: String) : List<Booking> {
        checkBookingDate(bookingDate = bookingDate)
        return bookingRepository.findByBookingDateAndStatus(bookingDate = bookingDate,
            availableCode = BookingStatusCode.AVAILABLE.code)
    }

    fun read(bookingDate: String, seatId: Int) : List<Booking> {
        checkSeatId(seatId = seatId)
        checkBookingDate(bookingDate = bookingDate)
        return bookingRepository.findBySeatIdAndBookingDateAndStatus(seatId = seatId,
            bookingDate = bookingDate, availableCode = BookingStatusCode.AVAILABLE.code)
    }

    fun read(status : BookingStatusCode) : List<Booking> {
        return bookingRepository.findByStatus(bookingStatusCode = status.code)
    }

    private fun checkBookingDate(bookingDate: String): LocalDateTime {
        try {
            return LocalDateTime.parse(bookingDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        } catch (e: Exception) {
            log.warn("Invalid booking Date : {}", bookingDate)
            throw InvalidBookingDateException()
        }
    }

    private fun checkSeatId(seatId: Int) {
        if (seatId > 50 || seatId <= 0) {
            log.warn("Invalid SeatId : {}", seatId)
            throw InvalidSeatIdException()
        }
    }
}
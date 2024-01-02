package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.exception.FailedReserveException
import com.hhplus.domain.exception.InvalidTicketException
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.presentation.booking.BookingStatusCode
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Component
class BookingProcessor(val bookingRepository: BookingRepository) {

    @Transactional
    fun reserve(bookings : List<Booking>, uuid : Long) : Booking {
        if(bookings.size != 1) {
            throw InvalidTicketException()
        }

        val booking : Booking = bookings[0]
        try {
            booking.status = BookingStatusCode.RESERVED.code
            bookingRepository.
            return booking
        } catch (e : Exception) {
            throw FailedReserveException()
        }
    }
}
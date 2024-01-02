package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.presentation.booking.BookingStatusCode
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BookingProcessor(val bookingReader: BookingReader) {

    @Transactional
    fun reserve(seatId : Int, bookingDate : String, uuid : Long) : Booking {
        bookingReader.read(seatId = seatId, bookingDate = bookingDate).let { bookings ->
            if(bookings.size != 1) {
                throw IllegalArgumentException()
            }

            val booking : Booking = bookings[0]

            booking.status = BookingStatusCode.RESERVED.code
            return booking
        }
    }
}
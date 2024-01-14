package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.exception.FailedReserveException
import com.hhplus.presentation.booking.BookingStatusCode
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BookingProcessor(val bookingReader: BookingReader) {

    private val log = KotlinLogging.logger("BookingProcessor")
    @Transactional
    fun reserve(seatId : Int, bookingDate : String, uuid : Long) : Booking {
        try {
            bookingReader.read(seatId = seatId, bookingDate = bookingDate).let { bookings ->
                if(bookings.size != 1) {
                    throw IllegalArgumentException()
                }

                val booking : Booking = bookings[0]

                booking.status = BookingStatusCode.RESERVED.code
                booking.userUuid = uuid
                return booking
            }
        } catch (e : IllegalArgumentException) {
            log.warn("Invalid Request : seatId : {}, bookingDate: {}, uuid : {}", seatId, bookingDate, uuid)
            throw FailedReserveException()
        }
        catch (e: Exception) {
            log.error(e.cause.toString())
            throw FailedReserveException()
        }
    }

}
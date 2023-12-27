package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.info.TicketInfo
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.domain.exception.InvalidTicketException
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class BookingProcessor(val ticketRepository: TicketRepository) {

    fun reserve(bookings : List<Booking>, uuid : Long) : Booking {
        if(bookings.size != 1) {
            throw InvalidTicketException()
        }

        val booking : Booking = bookings[0]
        val concertInfo = ConcertInfo(seatId = booking.seatId, date = booking.bookingDate)
        ticketRepository.saveReserveMap(concertInfo, TicketInfo(uuid = uuid, price = booking.price), 300, TimeUnit.SECONDS)
        return booking
    }
}
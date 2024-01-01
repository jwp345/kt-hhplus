package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.exception.FailedReserveException
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.info.AssignmentInfo
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.domain.exception.InvalidTicketException
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Component
class BookingProcessor(val ticketRepository: TicketRepository) {

    fun reserve(bookings : List<Booking>, uuid : Long) : Booking {
        if(bookings.size != 1) {
            throw InvalidTicketException()
        }

        val booking : Booking = bookings[0]
        try {
            ticketRepository.getLockAndReserveMap().run {
                val concertInfo = ConcertInfo(seatId = booking.seatId, date = booking.bookingDate)
                val assignmentInfo = AssignmentInfo(uuid = uuid, createdAt = LocalDateTime.now())
                lock.lock(4, TimeUnit.SECONDS)
                try {
                    if (map.contains(concertInfo)) {
                        throw RuntimeException()
                    }

                    map.put(concertInfo, assignmentInfo)
                } finally {
                    if (lock.isLocked && lock.isHeldByCurrentThread) {
                        lock.unlock()
                    }
                }
            }
        } catch (e : Exception) {
            throw FailedReserveException()
        }
        return booking
    }
}
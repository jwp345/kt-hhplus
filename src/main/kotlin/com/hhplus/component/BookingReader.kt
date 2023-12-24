package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.infrastructure.exception.InvalidDateException
import com.hhplus.infrastructure.exception.InvalidSeatIdException
import com.hhplus.presentation.booking.BookingStatusCode
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class BookingReader(val bookingRepository: BookingRepository, val ticketFactory: TicketFactory) {

    fun read(seatId : Int) : List<Booking> {
        checkSeatId(seatId = seatId)
        return bookingRepository.getDatesBySeatId(seatId = seatId, availableCode = BookingStatusCode.AVAILABLE.code)
            .filterNot { isReserved(seatId = seatId, bookingDate = it.bookingDate) }
    }

    fun read(bookingDate: String) : List<Booking> {
        checkBookingDate(bookingDate = bookingDate)
        return bookingRepository.getSeatIdsByBookingDate(bookingDate = bookingDate,
            availableCode = BookingStatusCode.AVAILABLE.code)
            .filterNot { isReserved(seatId = it.seatId, bookingDate = bookingDate) }
    }

    fun read(bookingDate: String, seatId: Int) : List<Booking> {
        checkSeatId(seatId = seatId)
        checkBookingDate(bookingDate = bookingDate)
        return bookingRepository.getIdAndPricesBySeatIdAndBookingDateAndStatus(seatId = seatId,
            bookingDate = bookingDate, availableCode = BookingStatusCode.AVAILABLE.code)
    }

    private fun checkBookingDate(bookingDate: String): LocalDateTime {
        try {
            return LocalDateTime.parse(bookingDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
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
        val cacheMap = ticketFactory.getLockAndReserveMap().mapCache
        return cacheMap.contains(ConcertInfo(seatId = seatId, date = bookingDate))
    }
}
package com.hhplus.application

import com.hhplus.component.BookingProcessor
import com.hhplus.component.BookingReader
import com.hhplus.domain.entity.Booking
import com.hhplus.domain.exception.FailedReserveException
import org.springframework.stereotype.Service

@Service
class BookingFacade (val bookingReader: BookingReader, val bookingProcessor: BookingProcessor) {

    fun findDatesAvailable(seatId : Int) : List<Booking> {
        return bookingReader.read(seatId = seatId)
    }

    fun findSeatsAvailable(date: String): List<Booking> {
        return bookingReader.read(date)
    }

    fun reserveSeat(seatId: Int, bookingDate: String, uuid: Long): Booking {
        try {
            return bookingProcessor.reserve(seatId = seatId, bookingDate = bookingDate, uuid = uuid)
        } catch (e : Exception) {
            throw FailedReserveException() // 흠 이것도 불편...
        }
    }
}


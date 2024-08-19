package com.hhplus.domain.repository

import com.hhplus.domain.entity.Booking
import java.time.LocalDateTime

interface BookingRepository {
    fun findBySeatIdAndStatus(seatId: Int, availableCode: Int) : List<Booking>

    fun findByBookingDateAndStatus(bookingDate: LocalDateTime, availableCode: Int) : List<Booking>

    fun findBySeatIdAndBookingDateAndStatus(seatId: Int, bookingDate: LocalDateTime, availableCode: Int) : List<Booking>

    fun findBySeatIdAndBookingDateAndStatusAndUserUuid(seatId: Int, bookingDate: LocalDateTime, availableCode: Int, userUuid : Long) : List<Booking>

    fun save(booking : Booking)

    fun findByStatus(availableCode : Int) : List<Booking>
}
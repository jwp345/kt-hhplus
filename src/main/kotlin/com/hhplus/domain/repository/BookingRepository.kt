package com.hhplus.domain.repository

import com.hhplus.domain.entity.Booking

interface BookingRepository {
    fun findBySeatIdAndStatus(seatId: Int, availableCode: Int) : List<Booking>

    fun findByBookingDateAndStatus(bookingDate: String, availableCode: Int) : List<Booking>

    fun findBySeatIdAndBookingDateAndStatus(seatId: Int, bookingDate: String, availableCode: Int) : List<Booking>

    fun findBySeatIdAndBookingDateAndStatusAndUserUuid(seatId: Int, bookingDate: String, availableCode: Int, userUuid : Long) : List<Booking>

    fun save(booking : Booking)

    fun findByStatus(availableCode : Int) : List<Booking>
}
package com.hhplus.domain.repository

import com.hhplus.domain.entity.Booking

interface BookingRepository {
    fun findBySeatIdAndStatus(seatId: Int, availableCode: Int) : List<Booking>

    fun findByBookingDateAndStatus(bookingDate: String, availableCode: Int) : List<Booking>

    fun findBySeatIdAndBookingDateAndStatus(seatId: Int, bookingDate: String, availableCode: Int) : List<Booking>

    fun save(booking : Booking)

    fun findByStatus(bookingStatusCode : Int) : List<Booking>
}
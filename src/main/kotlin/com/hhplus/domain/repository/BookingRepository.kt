package com.hhplus.domain.repository

import com.hhplus.domain.entity.Booking

interface BookingRepository {
    fun getDatesBySeatId(seatId: Int, availableCode: Int) : List<Booking>

    fun getSeatIdsByBookingDate(bookingDate: String, availableCode: Int) : List<Booking>

    fun getBookingBySeatIdAndBookingDateAndStatus(seatId: Int, bookingDate: String, availableCode: Int) : List<Booking>

    fun updateStatusBySeatIdAndBookingDate(bookingDate: String, seatId: Int, availableCode: Int) : Int
}
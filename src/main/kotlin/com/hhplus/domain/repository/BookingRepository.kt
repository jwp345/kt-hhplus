package com.hhplus.domain.repository

import com.hhplus.domain.entity.Booking

interface BookingRepository {
    fun getDatesBySeatId(seatId: Int, availableCode: Int) : List<Booking>

    fun getSeatIdsByBookingDate(bookingDate: String, availableCode: Int) : List<Booking>

    fun getIdAndPricesBySeatIdAndBookingDateAndStatus(seatId: Int, bookingDate: String, availableCode: Int) : List<Booking>
}
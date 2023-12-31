package com.hhplus.infrastructure.persistence

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.repository.BookingRepository
import org.springframework.data.jpa.repository.JpaRepository

interface BookingRepositoryImpl : JpaRepository<Booking, Long>, BookingRepository{

    override fun findBySeatIdAndStatus(seatId: Int, availableCode: Int): List<Booking>

    override fun findByBookingDateAndStatus(bookingDate: String, availableCode: Int): List<Booking>

    override fun findBySeatIdAndBookingDateAndStatus(
        seatId: Int,
        bookingDate: String,
        availableCode: Int
    ): List<Booking>

    override fun save(booking: Booking) {
        this.save(booking)
    }
}
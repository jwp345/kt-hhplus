package com.hhplus.infrastructure.persistence

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.repository.BookingRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface BookingRepositoryImpl : JpaRepository<Booking, Long>, BookingRepository{
    @Query("SELECT b FROM Booking b WHERE b.seatId = :seatId AND b.status = :availableCode")
    override fun getDatesBySeatId(seatId: Int, availableCode: Int) : List<Booking>

    @Query("SELECT b FROM Booking b WHERE b.bookingDate = :bookingDate AND b.status = :availableCode")
    override fun getSeatIdsByBookingDate(bookingDate: String, availableCode: Int) : List<Booking>

    @Query("SELECT b FROM Booking b WHERE b.seatId = :seatId AND b.status = :availableCode" +
            " AND b.bookingDate = :bookingDate")
    override fun getBookingBySeatIdAndBookingDateAndStatus(seatId: Int, bookingDate: String, availableCode: Int) : List<Booking>

    @Modifying
    @Query("update Booking b set b.status = :availableCode where b.bookingDate = :bookingDate AND b.seatId = :seatId")
    override fun updateStatusBySeatIdAndBookingDate(bookingDate: String, seatId: Int, availableCode: Int): Int
}
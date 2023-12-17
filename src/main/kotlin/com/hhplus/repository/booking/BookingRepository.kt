package com.hhplus.repository.booking

import com.hhplus.model.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BookingRepository : JpaRepository<Booking, Long>{
    @Query("SELECT b.bookingDate FROM Booking b WHERE b.seatId = :seatId AND (b.status = :availableCode OR " +
            "(b.status = :reservedCode AND b.reservedDate < :targetDate))")
    fun findDatesBySeatId(seatId: Int, availableCode: Int, reservedCode: Int, targetDate: String) : List<String>

    @Query("SELECT b.seatId FROM Booking b WHERE b.bookingDate = :bookingDate AND (b.status = :availableCode OR" +
            "(b.status = :reservedCode AND b.reservedDate < :targetDate))")
    fun findSeatIdByBookingDate(bookingDate: String, availableCode: Int, reservedCode: Int, targetDate: String) : List<Int>
}
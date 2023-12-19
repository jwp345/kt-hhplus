package com.hhplus.repository.booking

import com.hhplus.model.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface BookingRepository : JpaRepository<Booking, Long>{
    @Query("SELECT b.bookingDate FROM Booking b WHERE b.seatId = :seatId AND b.status = :availableCode")
    fun findDatesBySeatId(seatId: Int, availableCode: Int) : List<String>

    @Query("SELECT b.seatId FROM Booking b WHERE b.bookingDate = :bookingDate AND b.status = :availableCode")
    fun findSeatIdByBookingDate(bookingDate: String, availableCode: Int) : List<Int>

    @Query("SELECT b.id FROM Booking b WHERE b.seatId = :seatId AND b.status = :availableCode" +
            " AND b.bookingDate = :bookingDate")
    fun findIdsBySeatIdAndBookingDateAndStatus(seatId: Int, bookingDate: String, availableCode: Int) : List<Long>
}
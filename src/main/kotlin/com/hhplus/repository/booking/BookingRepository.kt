package com.hhplus.repository.booking

import com.hhplus.model.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BookingRepository : JpaRepository<Booking, Long>{
    @Query("SELECT b.availableDate FROM Booking b WHERE b.seatId = :seatId AND b.status = :status")
    fun findBySeatIdAndStatus(seatId: Int, status: Int) : List<String>

    @Query("SELECT b.seatId FROM Booking b WHERE b.availableDate = :availableDate AND b.status = :status")
    fun findByAvailableDateAndStatus(availableDate: String, status: Int) : List<Int>
}
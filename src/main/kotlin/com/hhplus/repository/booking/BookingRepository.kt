package com.hhplus.repository.booking

import com.hhplus.model.Booking
import org.springframework.data.jpa.repository.JpaRepository

interface BookingRepository : JpaRepository<Booking, Long>{
    fun findBySeatIdAndStatus(seatId: Int, status: Int) : List<Booking>
}
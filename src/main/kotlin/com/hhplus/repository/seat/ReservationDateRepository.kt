package com.hhplus.repository.seat

import com.hhplus.model.ReservationStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationDateRepository : JpaRepository<ReservationStatus, Long>{
    fun findBySeatId(seatId: Int)
}
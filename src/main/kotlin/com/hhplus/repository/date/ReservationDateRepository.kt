package com.hhplus.repository.date

import com.hhplus.model.ReservationStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationDateRepository : JpaRepository<ReservationStatus, Long>{
    fun findBySeatIdAndStatus(seatId: Int, status: Int) : List<ReservationStatus>
}
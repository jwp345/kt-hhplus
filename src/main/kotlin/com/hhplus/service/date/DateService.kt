package com.hhplus.service.date

import com.hhplus.controller.date.AvailableDateDto
import com.hhplus.model.ReservationStatus
import com.hhplus.repository.seat.ReservationDateRepository
import org.springframework.stereotype.Service

@Service
class DateService (val reservationDateRepository: ReservationDateRepository) {
    fun findAvailableDate(seatId : Int) : AvailableDateDto {
        var entity : ReservationStatus = reservationDateRepository.findBySeatId(seatId)
    }
}
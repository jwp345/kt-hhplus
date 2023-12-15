package com.hhplus.service.date

import com.hhplus.common.ReservationStatusCode
import com.hhplus.controller.date.AvailableDateDto
import com.hhplus.repository.date.ReservationDateRepository
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class DateService (val reservationDateRepository: ReservationDateRepository) {
    fun findAvailableDate(seatId : Int) : AvailableDateDto {
        return AvailableDateDto(reservationDateRepository.findBySeatIdAndStatus(seatId, ReservationStatusCode.AVAILABLE.code)
            .stream().map { list -> list.availableDate }
            .collect(Collectors.toList()))
    }
}
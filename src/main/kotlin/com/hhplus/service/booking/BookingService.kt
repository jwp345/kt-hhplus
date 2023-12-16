package com.hhplus.service.booking

import com.hhplus.common.BookingStatusCode
import com.hhplus.controller.booking.AvailableDateDto
import com.hhplus.repository.booking.BookingRepository
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class BookingService (val reservationDateRepository: BookingRepository) {
    fun findDatesAvailable(seatId : Int) : AvailableDateDto {
        return AvailableDateDto(reservationDateRepository.findBySeatIdAndStatus(seatId, BookingStatusCode.AVAILABLE.code)
            .stream().map { list -> list.availableDate }
            .collect(Collectors.toList()))
    }
}
package com.hhplus.presentation.booking

import com.hhplus.presentation.ApiResponse
import com.hhplus.application.BookingFacade
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/booking")
class BookingRestController (val bookingFacade: BookingFacade){

    @GetMapping("/dates/available")
    fun findDatesAvailable(@RequestParam(required = true) seatId: Int) : ApiResponse<DatesAvailableResponse?> {
        return ApiResponse.ok(DatesAvailableResponse(
            bookingFacade.findDatesAvailable(seatId = seatId).stream()
            .map { booking -> booking.bookingDate }
            .toList()))
    }

    @GetMapping("/seats/available")
    fun findSeatsAvailable(@RequestParam(required = true) date: String) : ApiResponse<SeatsAvailableResponse?> {
        return ApiResponse.ok(SeatsAvailableResponse(
            bookingFacade.findSeatsAvailable(date = date).stream()
                .map { booking -> booking.seatId }
                .toList()))
    }

    @PostMapping("/seat/reservation")
    fun reserveSeat(@RequestBody reservationCommand: ReservationCommand) : ApiResponse<ReservationResponse> {
        return bookingFacade.reserveSeat(seatId = reservationCommand.seatId, bookingDate = reservationCommand.date,
            uuid = reservationCommand.uuid).let { booking ->
            ApiResponse.ok(
                ReservationResponse(
                    seatId = booking.seatId, reservedDate = LocalDateTime.now(),
                    userId = reservationCommand.uuid, concertDate = booking.bookingDate
                )
            )
        }
    }

}
package com.hhplus.presentation.booking

import com.hhplus.presentation.ApiResponse
import com.hhplus.application.BookingFacade
import com.hhplus.component.DateTimeParser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/v1/booking")
class BookingRestController (val bookingFacade: BookingFacade, val dateTimeParser: DateTimeParser){

    @GetMapping("/dates/available")
    fun findDatesAvailable(@RequestParam(required = true) seatId: Int) : ApiResponse<DatesAvailableResponse?> {
        return ApiResponse.ok(DatesAvailableResponse(
            bookingFacade.findDatesAvailable(seatId = seatId).stream()
            .map { booking -> dateTimeParser.dateTimeToString(booking.bookingDate) }
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
        val uuid = (SecurityContextHolder.getContext().authentication.principal as UserDetails).username.toLong()
        return bookingFacade.reserveSeat(seatId = reservationCommand.seatId, bookingDate = reservationCommand.date,
            uuid = uuid).let { booking ->
            ApiResponse.ok(
                ReservationResponse(
                    seatId = booking.seatId, reservedDate = LocalDateTime.now(),
                    uuid = uuid,
                    bookingDate = dateTimeParser.dateTimeToString(booking.bookingDate)
                )
            )
        }
    }

}
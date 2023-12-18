package com.hhplus.controller.booking

import com.hhplus.controller.ApiResponse
import com.hhplus.service.booking.BookingService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/booking")
class BookingRestController (val bookingService: BookingService){

    @GetMapping("/dates/available")
    fun findDatesAvailable(@RequestParam(required = true) seatId: Int) : ApiResponse<DatesAvailableDto?> {
        return ApiResponse.ok(bookingService.findDatesAvailable(seatId))
    }

    @GetMapping("/seats/available")
    fun findSeatsAvailable(@RequestParam(required = true) date: String) : ApiResponse<SeatsAvailableDto?> {
        return ApiResponse.ok(bookingService.findSeatsAvailable(date))
    }

//    @PostMapping("/seat/reservation")
//    fun reserveSeat(@RequestBody reservationRequest: ReservationRequest) : ApiResponse<ReservationDto> {
//        return try {
//            ApiResponse.ok(bookingService.reserveSeat())
//        } catch (e: IllegalArgumentException) {
//            ApiResponse.error(4002, "유효한 날짜 형식이 아닙니다.")
//        }
//    }

}
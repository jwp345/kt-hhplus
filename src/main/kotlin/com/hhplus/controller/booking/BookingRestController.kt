package com.hhplus.controller.booking

import com.hhplus.controller.ApiResponse
import com.hhplus.service.booking.BookingService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/booking")
class BookingRestController (val bookingService: BookingService){

    @GetMapping("/dates/available")
    fun findDatesAvailable(@RequestParam(required = true) seatId: Int) : ApiResponse<AvailableDateDto> {
        return ApiResponse.ok(bookingService.findDatesAvailable(seatId))
    }

}
package com.hhplus.controller.booking

import com.hhplus.controller.ApiResponse
import com.hhplus.service.booking.BookingService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.lang.IllegalArgumentException

@RestController
@RequestMapping("/api/v1/booking")
class BookingRestController (val bookingService: BookingService){

    @GetMapping("/dates/available")
    fun findDatesAvailable(@RequestParam(required = true) seatId: Int) : ApiResponse<AvailableDateDto?> {
        try {
            return ApiResponse.ok(bookingService.findDatesAvailable(seatId))
        } catch (e: IllegalArgumentException) {
            return ApiResponse(4001, HttpStatus.BAD_REQUEST, "좌석은 1~50까지만 존재합니다.", null)
        }
    }

}
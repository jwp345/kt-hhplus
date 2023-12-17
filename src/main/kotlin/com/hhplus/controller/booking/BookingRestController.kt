package com.hhplus.controller.booking

import com.hhplus.controller.ApiResponse
import com.hhplus.service.booking.BookingService
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
        return try {
            ApiResponse.ok(bookingService.findDatesAvailable(seatId))
        } catch (e: IllegalArgumentException) {
            ApiResponse.error(4001, "유효한 입력이 아닙니다.")
        }
    }

//    @GetMapping("/seats/availabe")
//    fun findSeatsAvailable(@RequestParam(required = true) date: String) : ApiResponse<> {
//        return try {
//            ApiResponse.ok(bookingService.findSeatsAvailable(date))
//        }
//    }

}
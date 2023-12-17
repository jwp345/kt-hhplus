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
    fun findDatesAvailable(@RequestParam(required = true) seatId: Int) : ApiResponse<DatesAvailableDto?> {
        return try {
            ApiResponse.ok(bookingService.findDatesAvailable(seatId))
        } catch (e: IllegalArgumentException) {
            ApiResponse.error(4001, "좌석의 숫자는 1~50이여야 합니다.")
        }
    }

    @GetMapping("/seats/available")
    fun findSeatsAvailable(@RequestParam(required = true) date: String) : ApiResponse<SeatsAvailableDto?> {
        return try {
            ApiResponse.ok(bookingService.findSeatsAvailable(date))
        } catch (e: IllegalArgumentException) {
            ApiResponse.error(4002, "유효한 날짜 형식이 아닙니다.")
        }
    }

}
package com.hhplus.controller.date

import com.hhplus.controller.ApiResponse
import com.hhplus.service.date.DateService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/date")
class DateRestController (val dateService: DateService){

    @GetMapping("/available")
    fun generateToken(@RequestParam(required = true) seatId: Int) : ApiResponse<AvailableDateDto> {
        return ApiResponse.ok(dateService.findAvailableDate(seatId))
    }
}
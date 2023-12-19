package com.hhplus.controller.booking

import java.time.LocalDateTime

data class ReservationDto(val seatId : Int, val bookingDate : String, val userId : Long, val reservedDate : LocalDateTime?) {
}
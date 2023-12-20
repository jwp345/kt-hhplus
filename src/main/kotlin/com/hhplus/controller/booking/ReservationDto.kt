package com.hhplus.controller.booking

import java.time.LocalDateTime

data class ReservationDto(val seatId : Int, val concertDate : String, val userId : Long, val reservedDate : LocalDateTime?) {
}
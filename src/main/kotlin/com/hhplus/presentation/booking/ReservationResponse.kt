package com.hhplus.presentation.booking

import java.time.LocalDateTime

data class ReservationResponse(val seatId : Int, val concertDate : String, val userId : Long, val reservedDate : LocalDateTime?) {
}
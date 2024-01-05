package com.hhplus.presentation.booking

import java.time.LocalDateTime

data class ReservationResponse(val seatId : Int, val bookingDate : String, val uuid : Long, val reservedDate : LocalDateTime?) {
}
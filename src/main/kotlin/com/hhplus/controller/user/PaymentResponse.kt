package com.hhplus.controller.user

import java.time.LocalDateTime

data class PaymentResponse(val concertDate: String, val seatId: Int, val userId : Long, val paymentDate: LocalDateTime,
    val cost: Long, val balance : Long) {
}
package com.hhplus.presentation.payment

import java.time.LocalDateTime

data class PaymentResponse(val bookingDate: String, val seatId: Int, val uuid : Long, val paymentDate: LocalDateTime,
    val cost: Long) {
}
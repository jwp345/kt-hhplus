package com.hhplus.presentation.payment


data class PaymentCommand(val bookingDate: String, val seatId: Int, val uuid : Long) {
}
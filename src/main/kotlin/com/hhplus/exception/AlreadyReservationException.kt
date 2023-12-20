package com.hhplus.exception

class AlreadyReservationException : InvalidException() {
    override val code: Int = 4003
    override val message: String = "이미 예약된 좌석입니다."
}
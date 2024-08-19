package com.hhplus.domain.exception

class FailedFindBookingException : FailedException() {
    override val code: Int
        get() = 5002
    override val message: String
        get() = "결제가 이미 완료 된 예약이거나 예약이 만료된 티켓이 포함되어 있습니다."
}
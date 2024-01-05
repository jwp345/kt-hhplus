package com.hhplus.domain.exception

class FailedFindBookingException : FailedException() {
    override val code: Int
        get() = 5002
    override val message: String
        get() = "결제가 이미 완료 된 예약이거나 유효하지 않은 예약 정보 입니다."
}
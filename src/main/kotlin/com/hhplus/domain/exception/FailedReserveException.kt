package com.hhplus.domain.exception

class FailedReserveException : FailedException() {
    override val code: Int
        get() = 5002

    override val message: String
        get() = "예약이 실패 하였습니다."
}
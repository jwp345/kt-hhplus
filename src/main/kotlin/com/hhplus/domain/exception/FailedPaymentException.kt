package com.hhplus.domain.exception

class FailedPaymentException : FailedException() {
    override val code: Int
        get() = 5000

    override val message: String
        get() = "결제가 실패 하였습니다."
}
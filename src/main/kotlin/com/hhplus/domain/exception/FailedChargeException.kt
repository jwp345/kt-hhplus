package com.hhplus.domain.exception

class FailedChargeException : FailedException(){
    override val code: Int
        get() = 5001
    override val message: String
        get() = "충전에 실패 하였습니다."
}
package com.hhplus.domain.exception

class FailedCreateTokenException : FailedException(){
    override val code: Int
        get() = 5002

    override val message: String
        get() = "토큰 생성이 실패 하였습니다."
}
package com.hhplus.infrastructure.exception

class InvalidTokenAuthenticationException : InvalidException() {
    override val code: Int = 4007
    override val message: String = "토큰과 사용자가 다릅니다."
}
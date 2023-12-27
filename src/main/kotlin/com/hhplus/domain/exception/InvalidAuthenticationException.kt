package com.hhplus.domain.exception

class InvalidAuthenticationException : InvalidException() {
    override val code: Int = 4005
    override val message: String = "유효한 사용자가 아닙니다."
}
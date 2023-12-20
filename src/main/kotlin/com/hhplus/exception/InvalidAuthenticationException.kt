package com.hhplus.exception

class InvalidAuthenticationException : InvalidException() {
    override val code: Int = 4005
    override val message: String = "유효한 인증 정보가 아닙니다."
}
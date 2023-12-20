package com.hhplus.exception

class InvalidSeatIdException : InvalidException() {
    override val code: Int = 4001
    override val message: String = "좌석의 숫자는 1~50이여야 합니다."
}
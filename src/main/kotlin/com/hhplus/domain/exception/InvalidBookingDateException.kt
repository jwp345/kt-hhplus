package com.hhplus.domain.exception

class InvalidBookingDateException : InvalidException(){
    override val code: Int = 4002
    override val message: String = "유효한 날짜 형식이 아닙니다."
}
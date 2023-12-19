package com.hhplus.exception

class InvalidDateException : InvalidInputException(){
    override val code: Int = 4002
    override val message: String = "유효한 날짜 형식이 아닙니다."
}
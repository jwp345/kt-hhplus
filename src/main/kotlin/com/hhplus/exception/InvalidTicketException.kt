package com.hhplus.exception

class InvalidTicketException : InvalidInputException(){
    override val code: Int = 4004
    override val message: String = "유효한 티켓의 정보가 아닙니다."
}
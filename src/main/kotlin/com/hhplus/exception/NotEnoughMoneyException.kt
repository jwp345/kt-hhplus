package com.hhplus.exception

class NotEnoughMoneyException : InvalidException(){
    override val code: Int = 4006
    override val message: String = "잔액이 부족 합니다."
}
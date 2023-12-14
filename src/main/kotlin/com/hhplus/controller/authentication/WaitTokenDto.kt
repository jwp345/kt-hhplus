package com.hhplus.controller.authentication


data class WaitTokenDto(
    val userUUID : String,
    val waitOrder : Int,
    val token : String
)
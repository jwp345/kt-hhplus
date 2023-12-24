package com.hhplus.presentation.authentication


data class WaitTokenResponse(
    val userUUID : String,
    val waitOrder : Long,
    val token : String
)
package com.hhplus.presentation.authentication


data class WaitTokenResponse(
    val userUUID : Long,
    val waitOrder : Long,
    val token : String
)
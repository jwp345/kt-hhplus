package com.hhplus.domain.repository

import com.hhplus.infrastructure.security.WaitToken
import java.util.concurrent.TimeUnit

interface ValidWaitTokenRepository {
    fun add(token: WaitToken, ttl: Long, timeUnit: TimeUnit)
    fun getSize() : Int
    fun contains(token : WaitToken) : Boolean
    fun expireToken(token: WaitToken)
}
package com.hhplus.domain.repository

import com.hhplus.infrastructure.security.WaitToken

interface ValidWaitTokenRepository {
    fun add(token : WaitToken)
    fun remove(token : WaitToken)
    fun getSize() : Int
    fun contains(token : WaitToken) : Boolean
}
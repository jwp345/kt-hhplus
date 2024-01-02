package com.hhplus.domain.repository

import com.hhplus.infrastructure.security.WaitToken


interface WaitQueueRepository {
    fun add(token : WaitToken)

    fun pop() : WaitToken
}
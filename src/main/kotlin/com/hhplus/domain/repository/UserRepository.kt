package com.hhplus.domain.repository

import com.hhplus.domain.entity.User


interface UserRepository {
    fun findByUuid(uuid: Long) : User?
    fun updateBalanceByUuid(uuid: Long, balance: Long) : Int
}
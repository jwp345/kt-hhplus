package com.hhplus.application

import com.hhplus.component.UserReader
import com.hhplus.domain.entity.User
import org.springframework.stereotype.Service

@Service
class UserFacade (val userReader: UserReader) {
    fun chargeMoney(uuid: Long, amount: Long) : User {
        return userReader.read(uuid = uuid)
            .also { user ->
            user.balance += amount
        }
    }

    fun checkBalance(uuid: Long) : User {
        return userReader.read(uuid = uuid)
    }

}
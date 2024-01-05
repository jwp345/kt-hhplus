package com.hhplus.application

import com.hhplus.component.UserProcessor
import com.hhplus.component.UserReader
import com.hhplus.domain.entity.User
import org.springframework.stereotype.Service

@Service
class UserFacade (val userReader: UserReader, val userProcessor: UserProcessor) {
    fun chargeMoney(uuid: Long, amount: Long) : User {
        userReader.read(uuid = uuid).let { user ->
            user.balance += amount
            userProcessor.chargeMoney(uuid = uuid, balance = user.balance)
            return user
        }
    }

    fun checkBalance(uuid: Long) : User {
        return userReader.read(uuid = uuid)
    }

}
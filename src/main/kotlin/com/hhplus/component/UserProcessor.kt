package com.hhplus.component

import com.hhplus.domain.exception.FailedChargeException
import com.hhplus.domain.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class UserProcessor(val userRepository: UserRepository) {

    fun chargeMoney(uuid: Long, balance: Long) : Int{
        try {
            return userRepository.updateBalanceByUuid(uuid = uuid, balance = balance)
        } catch (e : Exception) {
            throw FailedChargeException()
        }
    }
}
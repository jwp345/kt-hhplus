package com.hhplus.component

import com.hhplus.domain.exception.FailedChargeException
import com.hhplus.domain.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class UserProcessor(val userRepository: UserRepository) {

    @Transactional
    fun chargeMoney(uuid: Long, balance: Long) {
        try {
            userRepository.findByUuid(uuid = uuid)?.apply {
                this.balance = balance
            } ?: throw FailedChargeException()
        } catch (e : Exception) {
            throw FailedChargeException()
        }
    }
}
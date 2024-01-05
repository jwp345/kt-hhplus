package com.hhplus.component

import com.hhplus.domain.exception.FailedChargeException
import com.hhplus.domain.repository.UserRepository
import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class UserProcessor(val userRepository: UserRepository) {
    private val log = KotlinLogging.logger("UserProcessor")
    @Transactional
    fun chargeMoney(uuid: Long, balance: Long) {
        try {
            userRepository.findByUuid(uuid = uuid)?.apply {
                this.balance = balance
            } ?: throw FailedChargeException()
        } catch (e : Exception) {
            log.error("Charge Error : uuid : {}, balance : {}", uuid, balance)
            log.error { e.cause.toString() }
            throw FailedChargeException()
        }
    }
}
package com.hhplus.component

import com.hhplus.domain.exception.InvalidTokenAuthenticationException
import com.hhplus.infrastructure.security.CustomUser
import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserValidator {

    private val log = KotlinLogging.logger("PaymentProcessor")

    fun checkTokenAndUser(userUuid : Long) {
        val tokenUuid = (SecurityContextHolder.getContext().authentication.principal as CustomUser).username.toLong()
        if(userUuid != tokenUuid) {
            log.warn("Invalid Request : userId: {}, tokenId : {}", userUuid, tokenUuid)
            throw InvalidTokenAuthenticationException()
        }
    }
}
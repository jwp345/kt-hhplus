package com.hhplus.component

import com.hhplus.domain.entity.User
import com.hhplus.domain.exception.InvalidAuthenticationException
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

    fun checkUuid(reqUUID : Long, user : User?) : Boolean {
        if (user == null) {
            throw InvalidAuthenticationException()
        }
        if(reqUUID != user.uuid) {
            log.error("Invalid User request reqUuid : {}, userUuid : {}", reqUUID, user.uuid)
            throw InvalidAuthenticationException()
        }
        return true
    }
}
package com.hhplus.component

import com.hhplus.domain.exception.InvalidTokenAuthenticationException
import com.hhplus.infrastructure.security.CustomUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserValidator {
    fun checkTokenAndUser(userUuid : Long) {
        val tokenUuid = (SecurityContextHolder.getContext().authentication.principal as CustomUser).username.toLong()
        if(userUuid != tokenUuid) {
            throw InvalidTokenAuthenticationException()
        }
    }
}
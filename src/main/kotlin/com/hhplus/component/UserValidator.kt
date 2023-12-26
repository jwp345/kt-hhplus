package com.hhplus.component

import com.hhplus.infrastructure.exception.InvalidTokenAuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class UserValidator {
    fun checkTokenAndUser(userUuid : Long) {
        val tokenUuid = (SecurityContextHolder.getContext().authentication.principal as UserDetails).username.toLong()
        if(userUuid != tokenUuid) {
            throw InvalidTokenAuthenticationException()
        }
    }
}
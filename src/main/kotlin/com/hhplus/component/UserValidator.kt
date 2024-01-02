package com.hhplus.component

import com.hhplus.domain.exception.InvalidTokenAuthenticationException
import com.hhplus.infrastructure.security.CustomUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserValidator {
    // 컴퍼넌트 맘에 안든다 남용이다 어노테이션으로 빼든지 하는 방향으로 가자
    fun checkTokenAndUser(userUuid : Long) {
        val tokenUuid = (SecurityContextHolder.getContext().authentication.principal as CustomUser).username.toLong()
        if(userUuid != tokenUuid) {
            throw InvalidTokenAuthenticationException()
        }
    }
}
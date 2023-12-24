package com.hhplus.component

import com.hhplus.infrastructure.exception.InvalidAuthenticationException
import com.hhplus.domain.entity.User
import com.hhplus.domain.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class UserReader(val userRepository: UserRepository) {
    fun read(uuid: Long) : User {
        return userRepository.findByUuid(uuid)?.let { user ->
            if (user.uuid != uuid) {
                throw InvalidAuthenticationException()
            }
            user
        } ?: throw InvalidAuthenticationException()
    }
}
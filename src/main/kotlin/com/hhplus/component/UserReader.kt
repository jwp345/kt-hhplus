package com.hhplus.component

import com.hhplus.domain.entity.User
import com.hhplus.domain.repository.UserRepository
import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class UserReader(val userRepository: UserRepository, val userValidator: UserValidator) {
    private val log = KotlinLogging.logger("UserReader")
    fun read(uuid: Long) : User {
        return userRepository.findByUuid(uuid).let { user ->
            userValidator.checkUuid(reqUUID = uuid, user = user)
            user!!
        }
    }
}
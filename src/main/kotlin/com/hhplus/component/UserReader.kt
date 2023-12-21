package com.hhplus.component

import com.hhplus.exception.InvalidAuthenticationException
import com.hhplus.model.User
import com.hhplus.repository.user.UserRepository
import org.springframework.stereotype.Component

@Component
class UserReader(val userRepository: UserRepository) {
    fun validCheckAndRead(userId : Long, uuid: String) : User {
        val user : User =  userRepository.findById(userId).get()
        if(!user.uuid.equals(uuid)) {
            throw InvalidAuthenticationException()
        }
        return user
    }
}
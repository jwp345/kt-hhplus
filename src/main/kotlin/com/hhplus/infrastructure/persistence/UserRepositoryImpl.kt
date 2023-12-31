package com.hhplus.infrastructure.persistence

import com.hhplus.domain.entity.User
import com.hhplus.domain.repository.UserRepository
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepositoryImpl : JpaRepository<User, Long>, UserRepository {

    override fun findByUuid(uuid: Long): User?

    override fun save(user: User) {
        this.save(user)
    }
}
package com.hhplus.infrastructure.persistence

import com.hhplus.domain.entity.User
import com.hhplus.domain.repository.UserRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface UserRepositoryImpl : JpaRepository<User, Long>, UserRepository {
    @Query("select u from User u where u.uuid = :uuid")
    override fun findByUuid(uuid: Long): User?

    @Modifying
    @Query("update User u set u.balance = :balance where u.uuid = :uuid")
    override fun updateBalanceByUuid(uuid: Long, balance: Long): Int
}
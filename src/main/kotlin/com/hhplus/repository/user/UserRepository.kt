package com.hhplus.repository.user

import com.hhplus.model.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
}
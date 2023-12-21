package com.hhplus.service.user

import com.hhplus.model.User
import com.hhplus.repository.user.UserRepository
import io.kotest.assertions.any
import io.kotest.core.spec.style.AnnotationSpec
import org.junit.jupiter.api.Assertions.*


internal class UserServiceTest : AnnotationSpec() {

    @Test
    fun `인증된 사용자일 경우 충전한다`() {
        // Given
        val userId = 1L
        val amount = 100
        val uuid = "valid_uuid"
        val userRepository = mockK<UserRepository>()
        val userService = UserService(userRepository)

        val user = User(userId = userId, balance = 50, uuid = "uuid")

        every { userRepository.findById(userId) } returns user
        every { userRepository.save(any()) } answers { firstArg() }

        // When
        userService.chargeMoney(userId, amount, uuid)

        // Then
        verify(exactly = 1) { userRepository.findById(userId) }
        user.balance shouldBe 150
    }

    @Test
    fun `chargeMoney should throw InvalidAuthenticationException if authentication is invalid`() {
        // Given
        val userId = 1L
        val amount = 100
        val invalidUuid = "invalid_uuid"
        val userRepository = mockk<UserRepository>()
        val userService = UserService(userRepository)

        val user = User(id = userId, balance = 50, uuid = "valid_uuid")

        every { userRepository.findById(userId) } returns user

        // When, Then
        shouldThrow<InvalidAuthenticationException> {
            userService.chargeMoney(userId, amount, invalidUuid)
        }
    }
}
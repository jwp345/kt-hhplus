package com.hhplus.component

import com.hhplus.exception.InvalidAuthenticationException
import com.hhplus.model.User
import com.hhplus.repository.user.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.every
import io.mockk.mockk
import java.util.*


internal class UserReaderTest : AnnotationSpec() {
    @Test
    fun `인증된 사용자가 아닐 경우 예외 발생`() {
        // Given
        val userId = 1L
        val amount = 100
        val uuid = UUID.randomUUID()
        val invalidUUid = UUID.randomUUID()
        val userRepository = mockk<UserRepository>()
        val userReader = UserReader(userRepository)

        val user = User(balance = 50, name = "jaewon", uuid = uuid)

        every { userRepository.findById(userId).get() } returns user

        // When, Then
        shouldThrow<InvalidAuthenticationException> {
            userReader.validCheckAndRead(userId, invalidUUid.toString())
        }
    }
}
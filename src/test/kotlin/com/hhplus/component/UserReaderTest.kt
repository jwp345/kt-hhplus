package com.hhplus.component

import com.hhplus.domain.exception.InvalidAuthenticationException
import com.hhplus.domain.entity.User
import com.hhplus.domain.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.every
import io.mockk.mockk


internal class UserReaderTest : AnnotationSpec() {
    @Test
    fun `인증요청한 사용자와 db의 사용자의 uuid가 다를 경우 예외 발생`() {
        // Given
        val uuid = 111L
        val invalidUUid = 112L
        val userRepository = mockk<UserRepository>()
        val userValidator = mockk<UserValidator>()
        val userReader = UserReader(userRepository, userValidator = userValidator)

        val user = User(balance = 50, name = "jaewon")
        user.uuid = invalidUUid

        every { userRepository.findByUuid(uuid) } returns user
        every { userValidator.checkUuid(reqUUID = uuid, user = user) } throws InvalidAuthenticationException()
        // When, Then
        shouldThrow<InvalidAuthenticationException> {
            userReader.read(uuid = uuid)
        }
    }

}
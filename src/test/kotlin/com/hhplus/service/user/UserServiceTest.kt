package com.hhplus.service.user

import com.hhplus.component.ReserveMapGetter
import com.hhplus.component.UserReader
import com.hhplus.exception.InvalidAuthenticationException
import com.hhplus.model.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*


internal class UserServiceTest : AnnotationSpec() {

    @Test
    fun `인증된 사용자일 경우 충전한다`() {
        // Given
        val userId = 1L
        val amount = 100
        val uuid = UUID.randomUUID()
        val userReader = mockk<UserReader>()
        val userService = UserService(userReader, mockk<ReserveMapGetter>())

        val user = User(balance = 50, name = "jaewon", uuid = uuid)

        every { userReader.validCheckAndRead(userId = userId, uuid = uuid.toString()) } returns user

        // When
        userService.chargeMoney(userId, amount, uuid.toString())

        // Then
        verify(exactly = 1) { userReader.validCheckAndRead(userId = userId, uuid = uuid.toString()) }
        user.balance shouldBe 150
    }


}
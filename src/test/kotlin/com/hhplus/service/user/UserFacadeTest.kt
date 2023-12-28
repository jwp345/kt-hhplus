package com.hhplus.service.user

import com.hhplus.application.UserFacade
import com.hhplus.component.UserProcessor
import com.hhplus.component.UserReader
import com.hhplus.domain.entity.User
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify


internal class UserFacadeTest : AnnotationSpec() {

    @Test
    fun `인증된 사용자일 경우 충전한다`() {
        // Given
        val userId = 1L
        val amount = 100L
        val userReader = mockk<UserReader>()
        val userProcessor = mockk<UserProcessor>()
        val userFacade = UserFacade(userReader, userProcessor)

        val user = User(balance = 50, name = "jaewon")

        every { userReader.read(uuid = userId) } returns user
        every { userProcessor.chargeMoney(uuid = userId, balance = amount + user.balance) } returns 1

        // When
        userFacade.chargeMoney(uuid = userId, amount)

        // Then
        verify(exactly = 1) { userReader.read(uuid = userId) }
        user.balance shouldBe 150
    }


}
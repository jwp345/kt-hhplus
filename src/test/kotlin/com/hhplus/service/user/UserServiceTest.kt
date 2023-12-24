//package com.hhplus.service.user
//
//import com.hhplus.application.UserFacade
//import com.hhplus.component.TicketFactory
//import com.hhplus.component.UserReader
//import com.hhplus.domain.entity.User
//import io.kotest.core.spec.style.AnnotationSpec
//import io.kotest.matchers.shouldBe
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import java.util.*
//
//
//internal class UserServiceTest : AnnotationSpec() {
//
//    @Test
//    fun `인증된 사용자일 경우 충전한다`() {
//        // Given
//        val userId = 1L
//        val amount = 100
//        val uuid = UUID.randomUUID()
//        val userReader = mockk<UserReader>()
//        val userFacade = UserFacade(userReader, mockk<TicketFactory>())
//
//        val user = User(balance = 50, name = "jaewon")
//
//        every { userReader.validCheckAndRead(userId = userId, uuid = uuid.toString()) } returns user
//
//        // When
//        userFacade.chargeMoney(uuid = user.uuid, amount)
//
//        // Then
//        verify(exactly = 1) { userReader.validCheckAndRead(userId = userId, uuid = uuid.toString()) }
//        user.balance shouldBe 150
//    }
//
//
//}
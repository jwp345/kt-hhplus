//package com.hhplus.component
//
//import com.hhplus.infrastructure.exception.InvalidAuthenticationException
//import com.hhplus.domain.entity.User
//import com.hhplus.domain.repository.UserRepository
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.core.spec.style.AnnotationSpec
//import io.mockk.every
//import io.mockk.mockk
//import java.util.*
//
//
//internal class UserReaderTest : AnnotationSpec() {
//    @Test
//    fun `인증된 사용자가 아닐 경우 예외 발생`() {
//        // Given
//        val userId = 1L
//        val uuid = "uuid"
//        val invalidUUid = "id"
//        val userRepository = mockk<UserRepository>()
//        val userReader = UserReader(userRepository)
//
//        val user = User(balance = 50, name = "jaewon")
//
//        every { userRepository.findByUuid(uuid) } returns user
//
//        // When, Then
//        shouldThrow<InvalidAuthenticationException> {
//            userReader.read(uuid = invalidUUid)
//        }
//    }
//}
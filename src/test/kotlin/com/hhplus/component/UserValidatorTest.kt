//package com.hhplus.component
//
//import com.hhplus.domain.exception.InvalidTokenAuthenticationException
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.core.spec.style.AnnotationSpec
//import io.mockk.every
//import io.mockk.mockk
//import org.junit.jupiter.api.Assertions.*
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.security.core.Authentication
//import org.springframework.security.core.context.SecurityContextHolder
//import org.springframework.security.core.userdetails.User
//
//internal class UserValidatorTest : AnnotationSpec(){
//
//    private val userValidator : UserValidator = UserValidator()
//    private val authentication : SecurityContextHolder = mockk<SecurityContextHolder>()
//    @Test
//    fun `토큰의 uuid와 로그인한 uuid가 다를 시 예외 발생`() {
//        every { authentication.getContext().authentication } returns UsernamePasswordAuthenticationToken(User("1", "", null), "", null)
//        shouldThrow<InvalidTokenAuthenticationException>{
//            userValidator.checkTokenAndUser(2L)
//        }
//    }
//}
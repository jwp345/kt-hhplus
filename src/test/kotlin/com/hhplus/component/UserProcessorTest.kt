package com.hhplus.component

import com.hhplus.domain.exception.FailedChargeException
import com.hhplus.domain.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

internal class UserProcessorTest : AnnotationSpec() {

    @Test
    fun `db 커넥션 에러 발생 시 에러 발생한다`() {
        val uuid = 123L
        val balance = 100L
        val userRepository = mockk<UserRepository>()
        val userProcessor = UserProcessor(userRepository)
        every { userRepository.findByUuid(uuid) }.throws(RuntimeException())

        shouldThrow<FailedChargeException> {
            userProcessor.chargeMoney(uuid = uuid, balance =  balance)
        }
    }
}
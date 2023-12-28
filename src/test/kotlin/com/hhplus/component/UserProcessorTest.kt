package com.hhplus.component

import com.hhplus.domain.exception.FailedChargeException
import com.hhplus.domain.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

internal class UserProcessorTest {
    private val userRepository : UserRepository = mock(UserRepository::class.java)
    private val userProcessor = UserProcessor(userRepository)

    @Test
    fun `충전 실패 시 충전 실패 예외를 발생시킨다`() {
        val uuid = 1L
        val balance = 50L
        given(userRepository.updateBalanceByUuid(uuid = uuid, balance = balance)).willThrow(RuntimeException())

        assertThrows(FailedChargeException::class.java){
            userProcessor.chargeMoney(uuid = uuid, balance = balance)
        }
    }
}
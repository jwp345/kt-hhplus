package com.hhplus.application

import com.hhplus.component.UserReader
import com.hhplus.domain.entity.User
import com.hhplus.domain.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserFacadeTest  {

    @Autowired
    private lateinit var userReader : UserReader

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userFacade: UserFacade

    lateinit var user : User

    @BeforeAll
    fun init() {
        user = User(name = "jaewon", balance = 50)
        userRepository.save(user)
    }

    @Test
    fun `인증된 사용자일 경우 충전한다`() {
        val amount = 100L
        userFacade.chargeMoney(uuid = user.uuid!!, amount = amount)
        assertThat(userReader.read(uuid = user.uuid!!).balance).isEqualTo(150)
    }

    @Test
    fun `인증된 사용자일 경우 잔액을 조회한다`() {
        assertThat(userFacade.checkBalance(uuid = user.uuid!!).balance).isEqualTo(50)
    }


}
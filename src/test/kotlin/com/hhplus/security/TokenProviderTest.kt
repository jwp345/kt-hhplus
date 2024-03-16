package com.hhplus.security
import com.hhplus.application.TokenProvider
import com.hhplus.domain.repository.WaitQueueRepository
import com.hhplus.infrastructure.security.WaitToken
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TokenProviderTest {

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    private lateinit var waitToken : WaitToken

    private val uuid : Long = 11L

    @Autowired
    private lateinit var waitQueueRepository: WaitQueueRepository

    @Test
    fun `토큰이 유효한 토큰 저장소에 존재 하지 않으면 false를 리턴 한다`() {
        assertThat(tokenProvider.validateToken(WaitToken(uuid = 1L, order = 2L, createAt = LocalDateTime.now().toEpochSecond(
            ZoneOffset.UTC))))
            .isFalse()
    }

    @Test
    fun `토큰이 유효한 토큰 저장소에 존재하면 true를 리턴 한다`() {
        val token = tokenProvider.createToken(uuid = uuid)
        assertThat(token).isNotNull()
        val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(token))

        ObjectInputStream(inputStream).use {
            this.waitToken = (it.readObject() as? WaitToken)!!
        }
        assertThat(tokenProvider.validateToken(token = waitToken)).isTrue()
    }

    @Test
    fun `유효 토큰 갯수 초과 부터는 대기열에 토큰이 저장된다`() {
        tokenProvider.createToken(23)
        tokenProvider.createToken(11)
        assertThat(waitQueueRepository.pop()).isNotNull
    }
}
package com.hhplus.security
import com.hhplus.infrastructure.security.TokenProvider
import com.hhplus.infrastructure.security.WaitToken
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TokenProviderTest {

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    private lateinit var waitToken : WaitToken

    private val uuid : Long = 11L

    @Order(1)
    @Test
    fun `토큰을 생성한다`(){
        val token = tokenProvider.createToken(uuid = uuid)
        assertThat(token).isNotNull()
        val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(token))

        ObjectInputStream(inputStream).use {
            this.waitToken = (it.readObject() as? WaitToken)!!
        }
        assertThat(waitToken.uuid).isEqualTo(uuid)
        assertThat(waitToken.order).isEqualTo(1L)
    }

    @Test
    fun `토큰이 유효한 토큰 저장소에 존재 하지 않으면 false를 리턴 한다`() {
        assertThat(tokenProvider.validateToken(WaitToken(uuid = 1L, order = 2L, createAt = LocalDateTime.now())))
            .isFalse()
    }

    @Order(3)
    @Test
    fun `토큰이 유효한 토큰 저장소에 존재하면 true를 리턴 한다`() {
        assertThat(tokenProvider.validateToken(waitToken)).isTrue()
    }

    @Order(2)
    @Test
    fun `동시에 10개 토큰 요청 시 같은 토큰이 생성되어선 안된다`() {
        val executor = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(10)

        val tokens = Collections.synchronizedSet(mutableSetOf<String>())

        repeat(10) {
            executor.submit{
                threadRequest(tokens)
                latch.countDown()
            }
        }

        latch.await()
        executor.shutdown()
        assertThat(tokens.size).isEqualTo(10)
    }

    private fun threadRequest(tokens : MutableSet<String>) {
        val threadName = Thread.currentThread().name
        println("Processing request on thread: $threadName")
        tokens.add(tokenProvider.createToken(uuid = uuid))
    }
}
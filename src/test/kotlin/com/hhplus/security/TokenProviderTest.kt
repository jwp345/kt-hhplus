package com.hhplus.security
import com.hhplus.infrastructure.config.RedisConfig
import com.hhplus.infrastructure.security.CustomUser
import com.hhplus.infrastructure.security.TokenProvider
import com.hhplus.infrastructure.security.WaitToken
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TokenProviderTest {

    @Autowired
    private lateinit var tokenProvider: TokenProvider
    @Autowired
    private lateinit var redissonClient: RedissonClient
    @Autowired
    private lateinit var redisConfig: RedisConfig

    private lateinit var waitToken : WaitToken

    private val uuid : Long = 11L

    @BeforeAll
    internal fun init() {
        val token = tokenProvider.createToken(uuid = uuid)
        val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(token))

        ObjectInputStream(inputStream).use {
            waitToken = (it.readObject() as? WaitToken)!!
        }
    }

    @Test
    fun `생성한 토큰 검증`(){
        assertThat(waitToken.uuid).isEqualTo(uuid)
        assertThat(waitToken.order).isEqualTo(1L)
    }

    @Test
    fun `토큰으로부터 인증 정보를 가져온다`() {
        assertThat((tokenProvider.getAuthentication(waitToken).principal as CustomUser)
            .username.toLong()).isEqualTo(11L)
    }


    @Test
    fun `동시에 10개 토큰 요청 시 같은 토큰이 생성되어선 안된다`() {
        val executor = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(10)

        val tokens = mutableSetOf<String>()

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
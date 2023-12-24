package com.hhplus.security
import com.hhplus.infrastructure.config.RedisConfig
import com.hhplus.infrastructure.security.TokenProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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

    @BeforeEach
    internal fun init() {
        redissonClient.getAtomicLong(redisConfig.waitOrderKey).set(0L)
    }

    @Test
    fun `토큰을 생성한다`(){
        val token = tokenProvider.createToken(uuid = 11L)
        assertThat(token.uuid).isNotNull()
        assertThat(token.order).isEqualTo(1L)
    }

    @Test
    fun `동시에 10개 토큰 요청 테스트`() {
        val executor = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(10)

        val orders = Collections.synchronizedList(mutableListOf<Long>())

        repeat(10) {
            executor.submit{
                threadRequest(orders)
                latch.countDown()
            }
        }

        latch.await()
        executor.shutdown()
        val expectOrders = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)
        orders.sort()
        assertThat(orders).isEqualTo(expectOrders)
    }

    private fun threadRequest(orders : MutableList<Long>) {
        val threadName = Thread.currentThread().name
        println("Processing request on thread: $threadName")
        orders.add(tokenProvider.createToken(uuid = 11L).order)
    }
}
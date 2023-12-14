package com.hhplus.security
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
internal class TokenProviderTest {

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @Test
    fun `토큰을 생성한다`(){
        val token = tokenProvider.createToken()
        assertThat(token.userUUID).isNotEmpty()
        assertThat(token.waitOrder).isEqualTo(0)
    }

    @Test
    fun `동시에 10개 토큰 요청 테스트`() {
        val executor = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(10)

        val orders = Collections.synchronizedList(mutableListOf<Int>())

        repeat(10) {
            executor.submit{
                threadRequest(orders)
                latch.countDown()
            }
        }

        latch.await()
        executor.shutdown()
        val expectOrders = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        orders.sort()
        assertThat(orders).isEqualTo(expectOrders)
    }

    private fun threadRequest(orders : MutableList<Int>) {
        val threadName = Thread.currentThread().name
        println("Processing request on thread: $threadName")
        orders.add(tokenProvider.createToken().waitOrder)
    }
}
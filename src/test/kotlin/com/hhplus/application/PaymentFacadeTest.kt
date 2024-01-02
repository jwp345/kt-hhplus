package com.hhplus.application

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.entity.User
import com.hhplus.domain.info.AssignmentInfo
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.repository.*
import com.hhplus.infrastructure.security.WaitToken
import com.hhplus.presentation.booking.BookingStatusCode
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class PaymentFacadeTest {

    @Autowired
    lateinit var paymentFacade : PaymentFacade

    @Autowired
    lateinit var ticketRepository: TicketRepository

    @Autowired
    lateinit var userRepository : UserRepository

    @Autowired
    lateinit var validWaitTokenRepository: ValidWaitTokenRepository

    @Autowired
    lateinit var bookingRepository: BookingRepository

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    lateinit var user : User

    lateinit var concertInfo: ConcertInfo

    lateinit var waitToken: WaitToken

    val bookingDate = "2023-11-20 15:30"
    val seatId = 1

    @BeforeAll
    fun init() {
        concertInfo = ConcertInfo(seatId = seatId, date = bookingDate)
        user = User(name = "jaewon", balance = 10_000)
        userRepository.save(user)

        waitToken = WaitToken(uuid = user.uuid!!, order = 1, createAt = LocalDateTime.now())
        ticketRepository.getLockAndReserveMap().map[concertInfo] =
            AssignmentInfo(user.uuid!!, createdAt = LocalDateTime.now())
        validWaitTokenRepository.add(token = waitToken)
        bookingRepository.save(Booking(seatId = seatId, bookingDate = bookingDate, status = BookingStatusCode.AVAILABLE, price = 5000))
    }

    @Order(2)
    @Test
    fun `결제를 성공하면 토큰이 만료되어야 한다`() {
        assertEquals(validWaitTokenRepository.contains(token = waitToken), false)
    }

    @Order(3)
    @Test
    fun `결제를 하면 Booking 테이블에 상태가 반영 되어야 한다`() {
        assertEquals(bookingRepository.findBySeatIdAndBookingDateAndStatus(seatId = seatId, bookingDate = bookingDate,
            availableCode = BookingStatusCode.CONFIRMED.code).size, 1)
        assertEquals(bookingRepository.findBySeatIdAndBookingDateAndStatus(seatId = seatId, bookingDate = bookingDate,
            availableCode = BookingStatusCode.AVAILABLE.code).size, 0)
    }

    @Order(4)
    @Test
    fun `결제를 하면 유저의 잔액이 정상적으로 차감 되어야 한다`() {
        userRepository.findByUuid(user.uuid!!)?.let { assertEquals(it.balance, 5000) }
    }

    @Order(5)
    @Test
    fun `커밋된 후 비동기로 실행된 리스너가 동작하여야 한다`() {
        assertEquals(1, paymentRepository.findByUuid(user.uuid!!).size)
    }

    @Order(1)
    @Test
    fun `동시에 10번의 결제를 요청할 경우 하나의 요청만 성공해야 한다`() {
        val executor = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(10)

        val exceptionNum = AtomicInteger(0)

        repeat(10) {
            executor.submit{
                try {
                    paymentFacade.payMoney(concertInfos = listOf(concertInfo), waitToken = waitToken)
                } catch (e: Exception) {
                    exceptionNum.getAndIncrement()
                }
                latch.countDown()
            }
        }

        latch.await()
        executor.shutdown()
        Assertions.assertThat(exceptionNum.get()).isEqualTo(9)
    }
}
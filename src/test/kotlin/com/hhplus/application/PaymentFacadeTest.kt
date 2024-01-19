package com.hhplus.application

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.entity.User
import com.hhplus.domain.repository.*
import com.hhplus.infrastructure.security.WaitToken
import com.hhplus.presentation.booking.BookingStatusCode
import com.hhplus.presentation.payment.ConcertInfo
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class PaymentFacadeTest {

    @Autowired
    lateinit var paymentFacade : PaymentFacade

    @Autowired
    lateinit var userRepository : UserRepository

    @Autowired
    lateinit var validWaitTokenRepository: ValidWaitTokenRepository

    @Autowired
    lateinit var waitQueueRepository: WaitQueueRepository

    @Autowired
    lateinit var bookingRepository: BookingRepository

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    lateinit var user : User

    lateinit var concertInfo: ConcertInfo

    lateinit var waitToken: WaitToken

    val bookingDate = "2023-11-20 15:30"
    val seatId = 1
    var uuid : Long = 0

    @BeforeAll
    fun init() {
        // redis 초기화 어떻게 잘 할지 고민해보기
        concertInfo = ConcertInfo(seatId = seatId, date = bookingDate)
        user = User(name = "jaewon", balance = 10_000)
        userRepository.save(user)

        uuid = user.uuid!!
        waitToken = WaitToken(uuid = uuid, order = 1, createAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
        waitQueueRepository.add(token = WaitToken(uuid = uuid, order = 2, createAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
        validWaitTokenRepository.add(token = waitToken, ttl = 1, timeUnit = TimeUnit.HOURS)
        val booking = Booking(seatId = seatId, bookingDate = bookingDate, status = BookingStatusCode.RESERVED, price = 5000)
        booking.userUuid = uuid
        bookingRepository.save(booking)
    }

    @Order(2)
    @Test
    fun `결제를 성공하면 토큰이 만료되어야 한다`() {
        assertEquals(validWaitTokenRepository.contains(token = waitToken), false)
    }

    @Order(3)
    @Test
    fun `결제를 하면 Booking 테이블에 상태가 반영 되어야 한다`() {
        assertEquals(bookingRepository.findBySeatIdAndBookingDateAndStatusAndUserUuid(seatId = seatId, bookingDate = bookingDate,
            availableCode = BookingStatusCode.CONFIRMED.code, userUuid = uuid).size, 1)
        assertEquals(bookingRepository.findBySeatIdAndBookingDateAndStatusAndUserUuid(seatId = seatId, bookingDate = bookingDate,
            availableCode = BookingStatusCode.AVAILABLE.code, userUuid = uuid).size, 0)
    }

    @Order(4)
    @Test
    fun `결제를 하면 유저의 잔액이 정상적으로 차감 되어야 한다`() {
        userRepository.findByUuid(uuid)?.let { assertEquals(it.balance, 5000) }
    }

    @Order(5)
    @Test
    fun `커밋된 후 비동기로 실행된 리스너가 동작하여야 한다`() {
        assertEquals(1, paymentRepository.findByUuid(uuid).size)
    }

    @Order(6)
    @Test
    fun `유효한 토큰들 중 하나가 만료되면 대기열 토큰에서 하나가 삭제되고 유효한 토큰 대기열에 추가된다`() {
        assertEquals(validWaitTokenRepository.getSize(), 1)
        assertEquals(waitQueueRepository.pop(), null)
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
package com.hhplus.component

import com.hhplus.ScheduledConfig
import com.hhplus.domain.entity.Booking
import com.hhplus.domain.entity.User
import com.hhplus.domain.exception.FailedFindBookingException
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.domain.repository.UserRepository
import com.hhplus.domain.repository.ValidWaitTokenRepository
import com.hhplus.infrastructure.security.WaitToken
import com.hhplus.presentation.booking.BookingStatusCode
import com.hhplus.presentation.payment.ConcertInfo
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.internal.verification.VerificationModeFactory.atLeast
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.data.auditing.AuditingHandler
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference


@SpringJUnitConfig(ScheduledConfig::class)
@SpringBootTest
internal class ExpiredBookingCleanSchedulerTest {

    @SpyBean
    private lateinit var expiredBookingCleanScheduler: ExpiredBookingCleanScheduler

    @Autowired
    private lateinit var bookingRepository: BookingRepository

    @SpyBean
    private lateinit var auditingHandler: AuditingHandler

    @Autowired
    private lateinit var paymentProcessor: PaymentProcessor

    @Autowired
    private lateinit var validWaitTokenRepository: ValidWaitTokenRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `스케줄러는 1분마다 돌아야 한다`() {
        await()
            .atMost(1, TimeUnit.MINUTES)
            .untilAsserted { verify(expiredBookingCleanScheduler, atLeast(1)).cleanExpiredBooking() }
    }

    @Test
    fun `스케줄러가 돌면 5분이 지난 예약 정보를 사용가능으로 변경해야 한다`() {
        auditingHandler.setDateTimeProvider { Optional.of(LocalDateTime.now().minusMinutes(5)) }

        Booking(seatId = 1, bookingDate = "2023-11-20 11:15", status = BookingStatusCode.RESERVED, price = 50)
            .apply {
                bookingRepository.save(this)
            }
        assertThat(bookingRepository.findByStatus(BookingStatusCode.RESERVED.code)[0].modifiedAt).isBefore(LocalDateTime.now().minusMinutes(5))
        expiredBookingCleanScheduler.cleanExpiredBooking()
        assert(bookingRepository.findByStatus(BookingStatusCode.AVAILABLE.code).size == 1)
    }

    @Test
    fun `5분이 지나면 만료된 것으로 판단한다`() {
        assertThat(expiredBookingCleanScheduler.isExpired(modifiedAt = LocalDateTime.now().minusMinutes(5),
            now = LocalDateTime.now())).isTrue()
    }

    @Test
    fun `결제와 충돌날 경우`() {
        /* given */
        auditingHandler.setDateTimeProvider { Optional.of(LocalDateTime.now().minusMinutes(6)) }
        val e = AtomicReference<Throwable?>()
        bookingRepository.save(Booking(seatId = 1, bookingDate = "2023-11-12 17:00", price = 5000, status = BookingStatusCode.RESERVED))
        bookingRepository.save(Booking(seatId = 2, bookingDate = "2023-11-13 18:00", price = 3000, status = BookingStatusCode.RESERVED))
        bookingRepository.save(Booking(seatId = 3, bookingDate = "2023-11-14 17:40", price = 4000, status = BookingStatusCode.RESERVED))
        val user = User(name = "Hello", balance = 1000000)
        userRepository.save(user = user)
        val waitToken = WaitToken(uuid = user.uuid!!, order = 1, createAt = LocalDateTime.now().toEpochSecond(
            ZoneOffset.UTC))
        validWaitTokenRepository.add(waitToken)

        // when
        CompletableFuture.allOf(
            CompletableFuture.runAsync { paymentProcessor.pay(concertInfos = listOf(ConcertInfo(date = "2023-11-12 17:00", seatId = 1),
                ConcertInfo(seatId = 2, date = "2023-11-13 18:00"), ConcertInfo(seatId = 3, date = "2023-11-14 17:40")),
                waitToken = waitToken
            ) },
            CompletableFuture.runAsync { expiredBookingCleanScheduler.cleanExpiredBooking() }
        ).exceptionally { throwable : Throwable ->
            e.set(throwable.cause)
            null
        }.join()

        //then
        assertThat(e.get()).isNotNull()
        assertThat(e.get()).isInstanceOf(FailedFindBookingException::class.java)
    }
}

package com.hhplus.component

import com.hhplus.ScheduledConfig
import com.hhplus.domain.entity.Booking
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.presentation.booking.BookingStatusCode
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
import java.util.*
import java.util.concurrent.TimeUnit


@SpringJUnitConfig(ScheduledConfig::class)
@SpringBootTest
internal class ExpiredBookingCleanSchedulerTest {

    @SpyBean
    private lateinit var expiredBookingCleanScheduler: ExpiredBookingCleanScheduler

    @Autowired
    private lateinit var bookingRepository: BookingRepository

    @SpyBean
    private lateinit var auditingHandler: AuditingHandler

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
}

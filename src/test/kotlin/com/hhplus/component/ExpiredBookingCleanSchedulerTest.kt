package com.hhplus.component

import com.hhplus.ScheduledConfig
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.internal.verification.VerificationModeFactory.atLeast
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.util.concurrent.TimeUnit


@SpringJUnitConfig(ScheduledConfig::class)
@SpringBootTest
internal class ExpiredBookingCleanSchedulerTest {

    @SpyBean
    private lateinit var expiredBookingCleanScheduler: ExpiredBookingCleanScheduler

    @Test
    fun `스케줄러는 1분마다 돌아야 한다`() {
        await()
            .atMost(1, TimeUnit.MINUTES)
            .untilAsserted { verify(expiredBookingCleanScheduler, atLeast(10)) }
    }

    // 5분이 지난 예약 건들을 예약 가능 상태로 업데이트 한다
}
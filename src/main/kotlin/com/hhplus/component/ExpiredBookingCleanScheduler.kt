package com.hhplus.component

import com.hhplus.presentation.booking.BookingStatusCode
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Component
class ExpiredBookingCleanScheduler(val bookingReader : BookingReader) {

    @Scheduled(cron = "\${cron.expired.clean}")
    @Transactional
    fun cleanExpiredBooking() {
        bookingReader.read(status = BookingStatusCode.RESERVED).forEach { booking ->
            booking.apply {
                if (isExpired(modifiedAt = modifiedAt, now = LocalDateTime.now())) {
                    status = BookingStatusCode.AVAILABLE.code
                }
            }
        }
    }

    fun isExpired(modifiedAt: LocalDateTime?, now: LocalDateTime?): Boolean {
        return Duration.between(modifiedAt, now).toMinutes() >= 5
    }
}
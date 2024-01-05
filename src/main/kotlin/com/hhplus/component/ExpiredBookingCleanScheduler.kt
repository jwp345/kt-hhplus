package com.hhplus.component


import com.hhplus.presentation.booking.BookingStatusCode
import mu.KotlinLogging.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Component
class ExpiredBookingCleanScheduler(val bookingReader : BookingReader) {

    private val log = logger("ExpiredBookingCleanSchedule")
    @Scheduled(cron = "\${cron.expired.clean}")
    @Transactional
    fun cleanExpiredBooking() {
        try {
            bookingReader.read(status = BookingStatusCode.RESERVED).forEach { booking ->
                booking.apply {
                    if (isExpired(modifiedAt = modifiedAt, now = LocalDateTime.now())) {
                        status = BookingStatusCode.AVAILABLE.code
                    }
                }
            }
        } catch (e : Exception) {
            log.error(e.cause.toString())
        }
    }

    fun isExpired(modifiedAt: LocalDateTime?, now: LocalDateTime?): Boolean {
        return Duration.between(modifiedAt, now).toMinutes() >= 5
    }
}
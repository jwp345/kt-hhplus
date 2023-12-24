package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.info.ReserveCacheInfo
import com.hhplus.domain.info.TicketInfo
import com.hhplus.infrastructure.exception.AlreadyReservationException
import com.hhplus.infrastructure.exception.InvalidTicketException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class BookingProcessor(val ticketFactory: TicketFactory) {

    @Retryable(value = [org.redisson.client.RedisTimeoutException::class],
        maxAttempts = 2, backoff = Backoff(delay = 2000))
    fun reserve(bookings : List<Booking>, uuid : Long) : Booking {
        if(bookings.size != 1) { // 티켓 유효성 검사
            throw InvalidTicketException()
        }

        val booking : Booking = bookings[0]
        val concertInfo = ConcertInfo(seatId = booking.seatId, date = booking.bookingDate)
        val lockAndMap : ReserveCacheInfo = ticketFactory.getLockAndReserveMap()
        return lockAndMap.run {
            lock.lock(4, TimeUnit.SECONDS)
            try {
                if (mapCache.contains(concertInfo)) {
                    throw AlreadyReservationException()
                }
                mapCache.put(concertInfo, TicketInfo(uuid = uuid, price = booking.price), 300, TimeUnit.SECONDS)
                booking
            } finally {
                if (lock.isLocked && lock.isHeldByCurrentThread) {
                    lock.unlock()
                }
            }
        }
    }
}
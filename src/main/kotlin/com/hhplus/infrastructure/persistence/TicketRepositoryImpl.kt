package com.hhplus.infrastructure.persistence

import com.hhplus.domain.entity.User
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.info.ReserveCacheInfo
import com.hhplus.domain.info.TicketInfo
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.infrastructure.config.RedisConfig
import com.hhplus.domain.exception.AlreadyReservationException
import com.hhplus.domain.exception.InvalidTicketException
import com.hhplus.domain.exception.NotEnoughMoneyException
import org.redisson.api.RedissonClient
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class TicketRepositoryImpl(val redissonClient: RedissonClient
, val redisConfig: RedisConfig) : TicketRepository {

    @Retryable(value = [org.redisson.client.RedisTimeoutException::class],
        maxAttempts = 2, backoff = Backoff(delay = 2000)
    )
    override fun getLockAndReserveMap() : ReserveCacheInfo {
        return ReserveCacheInfo(lock = redissonClient.getLock(redisConfig.reserveLockName),
            mapCache = redissonClient.getMapCache(redisConfig.cacheReserveKey))
    }

    override fun getTicketByConcertInfo(seatId : Int, bookingDate : String, user : User) : TicketInfo{
        return getLockAndReserveMap().mapCache.let { reserveMap ->
            reserveMap[ConcertInfo(seatId, bookingDate)]
                ?: throw InvalidTicketException()
        }.also { ticket ->
            if (user.balance < ticket.price) {
                throw NotEnoughMoneyException()
            }
            user.apply {
                balance -= ticket.price
            }
        }
    }

    @Retryable(value = [org.redisson.client.RedisTimeoutException::class],
        maxAttempts = 2, backoff = Backoff(delay = 2000))
    override fun saveReserveMap(concertInfo: ConcertInfo, ticketInfo: TicketInfo, ttl: Long, timeUnit: TimeUnit) {
        getLockAndReserveMap().run {
            lock.lock(4, TimeUnit.SECONDS)
            try {
                if (mapCache.contains(concertInfo)) {
                    throw AlreadyReservationException()
                }

                mapCache.put(concertInfo, ticketInfo, ttl, timeUnit)
            } finally {
                if (lock.isLocked && lock.isHeldByCurrentThread) {
                    lock.unlock()
                }
            }
        }
    }

}
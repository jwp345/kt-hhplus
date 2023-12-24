package com.hhplus.component

import com.hhplus.domain.entity.User
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.info.ReserveCacheInfo
import com.hhplus.domain.info.TicketInfo
import com.hhplus.infrastructure.config.RedisConfig
import com.hhplus.infrastructure.exception.InvalidTicketException
import com.hhplus.infrastructure.exception.NotEnoughMoneyException
import org.redisson.api.RedissonClient
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class TicketFactory(val redissonClient: RedissonClient, val redisConfig: RedisConfig) {

    @Retryable(value = [org.redisson.client.RedisTimeoutException::class],
        maxAttempts = 2, backoff = Backoff(delay = 2000))
    fun getLockAndReserveMap() : ReserveCacheInfo {
        return ReserveCacheInfo(lock = redissonClient.getLock(redisConfig.reserveLockName),
            mapCache = redissonClient.getMapCache(redisConfig.cacheReserveKey))
    }

    fun getTicket(seatId : Int, bookingDate : String, user : User) : TicketInfo {
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
}
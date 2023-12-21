package com.hhplus.component

import com.hhplus.config.RedisConfig
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class ReserveMapGetter(val redissonClient: RedissonClient, val redisConfig: RedisConfig) {

    @Retryable(value = [org.redisson.client.RedisTimeoutException::class], maxAttempts = 2, backoff = Backoff(delay = 2000))
    fun getLockAndReserveMap() : ReserveCacheInfo {
        val lock : RLock = redissonClient.getLock(redisConfig.reserveLockName)
        return ReserveCacheInfo(lock = lock, mapCache = redissonClient.getMapCache(redisConfig.cacheReserveKey))
    }
}
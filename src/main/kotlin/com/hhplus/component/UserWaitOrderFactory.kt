package com.hhplus.component

import com.hhplus.infrastructure.config.RedisConfig
import org.redisson.api.RedissonClient
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class UserWaitOrderFactory (val redissonClient: RedissonClient, val redisConfig: RedisConfig){

    @Retryable(value = [org.redisson.client.RedisTimeoutException::class],
        maxAttempts = 2, backoff = Backoff(delay = 2000))
    fun getWaitOrder() : Long {
        return redissonClient.getLock(redisConfig.waitOrderLockName).run {
            redissonClient.getAtomicLong(redisConfig.waitOrderKey).incrementAndGet()
        }
    }
}
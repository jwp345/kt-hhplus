package com.hhplus.infrastructure.persistence

import com.hhplus.domain.repository.WaitOrderRepository
import com.hhplus.infrastructure.config.RedisConfig
import org.redisson.api.RedissonClient
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class WaitOrderRepositoryImpl(val redissonClient: RedissonClient, val redisConfig: RedisConfig) : WaitOrderRepository{

    @Retryable(value = [org.redisson.client.RedisTimeoutException::class],
        maxAttempts = 2, backoff = Backoff(delay = 2000)
    )
    override fun getWaitOrder() : Long {
        return redissonClient.getLock(redisConfig.waitOrderLockName).run {
            redissonClient.getAtomicLong(redisConfig.waitOrderKey).incrementAndGet()
        }
    }
}
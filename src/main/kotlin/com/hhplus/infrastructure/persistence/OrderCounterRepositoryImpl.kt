package com.hhplus.infrastructure.persistence

import com.hhplus.domain.repository.OrderCounterRepository
import com.hhplus.infrastructure.config.RedisConfig
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

@Component
class OrderCounterRepositoryImpl(private val redissonClient: RedissonClient,
                                 private val redisConfig: RedisConfig) : OrderCounterRepository{

    override fun incrementAndGet(): Long {
        return redissonClient.getAtomicLong(redisConfig.orderCounterName).incrementAndGet()
    }

}
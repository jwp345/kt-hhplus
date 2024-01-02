package com.hhplus.infrastructure.persistence

import com.hhplus.domain.repository.WaitQueueRepository
import com.hhplus.infrastructure.config.RedisConfig
import com.hhplus.infrastructure.security.WaitToken
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

@Component
class WaitQueueRepositoryImpl(val redissonClient: RedissonClient, val redisConfig: RedisConfig) : WaitQueueRepository {

    override fun add(token: WaitToken) {
        redissonClient.getQueue<WaitToken>(redisConfig.waitQueueName).add(token)
    }

    override fun pop() : WaitToken {
        return redissonClient.getQueue<WaitToken>(redisConfig.waitQueueName).poll()
    }

}
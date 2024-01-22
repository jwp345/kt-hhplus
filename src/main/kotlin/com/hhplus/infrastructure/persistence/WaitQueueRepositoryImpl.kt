package com.hhplus.infrastructure.persistence

import com.hhplus.domain.repository.WaitQueueRepository
import com.hhplus.infrastructure.config.RedisConfig
import com.hhplus.infrastructure.security.WaitToken
import org.redisson.api.RQueue
import org.redisson.api.RedissonClient
import org.redisson.codec.TypedJsonJacksonCodec
import org.springframework.stereotype.Component

@Component
class WaitQueueRepositoryImpl(
    redissonClient: RedissonClient,
    redisConfig: RedisConfig) : WaitQueueRepository {

    private val waitQueue : RQueue<WaitToken> = redissonClient.getQueue(redisConfig.waitQueueName, TypedJsonJacksonCodec(WaitToken::class.java))
    override fun add(token: WaitToken) {
        waitQueue.add(token)
    }

    override fun pop() : WaitToken? {
        return waitQueue.poll()
    }

}
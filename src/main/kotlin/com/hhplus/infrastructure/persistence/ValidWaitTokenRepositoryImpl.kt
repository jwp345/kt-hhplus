package com.hhplus.infrastructure.persistence

import com.hhplus.component.TokenExpireEvent
import com.hhplus.domain.repository.ValidWaitTokenRepository
import com.hhplus.infrastructure.config.RedisConfig
import com.hhplus.infrastructure.security.WaitToken
import org.redisson.api.RMapCache
import org.redisson.api.RedissonClient
import org.redisson.api.map.event.EntryExpiredListener
import org.redisson.codec.TypedJsonJacksonCodec
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ValidWaitTokenRepositoryImpl(
    redissonClient: RedissonClient,
    redisConfig: RedisConfig,
    private val applicationEventPublisher: ApplicationEventPublisher
) : ValidWaitTokenRepository {

    private val validTokenMap: RMapCache<Long, WaitToken> = redissonClient.getMapCache(redisConfig.validMapName, TypedJsonJacksonCodec(Long::class.java, WaitToken::class.java))

    init {
        validTokenMap.addListener(EntryExpiredListener<Long, WaitToken> {
            applicationEventPublisher.publishEvent(TokenExpireEvent())
        })
    }

    override fun add(token: WaitToken, ttl: Long, timeUnit: TimeUnit) {
        validTokenMap.put(token.uuid, token, ttl, timeUnit)
    }

    override fun expireToken(token: WaitToken) {
        validTokenMap.remove(token.uuid)
        applicationEventPublisher.publishEvent(TokenExpireEvent())
    }

    override fun getSize(): Int {
        return validTokenMap.size
    }

    override fun contains(token: WaitToken): Boolean {
        return token == validTokenMap[token.uuid]
    }

}
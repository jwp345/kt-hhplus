package com.hhplus.infrastructure.persistence

import com.hhplus.domain.repository.ValidWaitTokenRepository
import com.hhplus.infrastructure.config.RedisConfig
import com.hhplus.infrastructure.security.WaitToken
import org.redisson.api.RSet
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

@Component
class ValidWaitTokenRepositoryImpl(private val redissonClient: RedissonClient,
                                   private val redisConfig: RedisConfig) : ValidWaitTokenRepository {

    override fun add(token: WaitToken) {
        getSet().add(token)
    }

    override fun remove(token: WaitToken) {
        getSet().remove(token)
    }

    override fun getSize() : Int {
        return getSet().size
    }

    override fun contains(token: WaitToken) : Boolean {
        return getSet().contains(token)
    }

    private fun getSet() : RSet<WaitToken> {
        return redissonClient.getSet(redisConfig.validSetName)
    }
}
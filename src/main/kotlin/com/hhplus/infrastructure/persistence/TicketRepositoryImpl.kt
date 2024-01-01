package com.hhplus.infrastructure.persistence


import com.hhplus.domain.info.ReserveCacheInfo
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.infrastructure.config.RedisConfig
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component

@Component
class TicketRepositoryImpl(val redissonClient: RedissonClient, val redisConfig: RedisConfig) : TicketRepository {

    override fun getLockAndReserveMap() : ReserveCacheInfo {
        return ReserveCacheInfo(lock = redissonClient.getLock(redisConfig.reserveLockName),
            map = redissonClient.getMap(redisConfig.cacheReserveKey))
    }

}
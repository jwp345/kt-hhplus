package com.hhplus.infrastructure.config

import com.hhplus.infrastructure.security.WaitToken
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.TypedJsonJacksonCodec
import org.redisson.config.Config
import org.redisson.spring.data.connection.RedissonConnectionFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisKeyValueAdapter
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories


@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@EnableRedisRepositories( // 추가해야됨
    enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP
)
class RedisConfig {

    lateinit var host: String
    var port: Int = 0
//    final val reserveLockName : String = "reserve-seat-lock"
//    final val cacheReserveKey : String = "seat-reservation"
    final val waitQueueName : String = "wait-queue"
    final val validMapName : String = "wait-token-map"
    final val orderCounterName : String = "order-counter"

    @Bean(destroyMethod = "shutdown")
    fun redissonClient(): RedissonClient {
        val config: Config = Config()
        config.useSingleServer()
            .setAddress("redis://$host:$port")
            .setDnsMonitoringInterval(-1)
        return Redisson.create(config)
    }

    @Bean
    fun redisConnectionFactory(redissonClient: RedissonClient): RedisConnectionFactory {
        return RedissonConnectionFactory(redissonClient)
    }

}
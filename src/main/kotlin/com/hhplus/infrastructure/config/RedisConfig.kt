package com.hhplus.infrastructure.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.spring.data.connection.RedissonConnectionFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory


@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
class RedisConfig {

    lateinit var host: String
    var port: Int = 0
    final val reserveLockName : String = "reserve-seat-lock"
    final val cacheReserveKey : String = "seat-reservation"
    final val waitOrderLockName : String = "wait-order-lock"
    final val waitOrderKey : String = "wait-order"
    final val waitOrderMapKey : String = "wait-order-map"

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
package com.hhplus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableJpaAuditing
@EnableRetry
class HhplusApplication

fun main(args: Array<String>) {
    runApplication<HhplusApplication>(*args)
}

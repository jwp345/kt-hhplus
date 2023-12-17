package com.hhplus

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class HhplusApplication

fun main(args: Array<String>) {
    runApplication<HhplusApplication>(*args)
}

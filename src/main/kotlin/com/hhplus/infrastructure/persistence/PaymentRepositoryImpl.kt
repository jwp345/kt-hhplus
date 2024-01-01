package com.hhplus.infrastructure.persistence

import com.hhplus.domain.entity.Payment
import com.hhplus.domain.repository.PaymentRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories
interface PaymentRepositoryImpl : JpaRepository<Payment, Long>, PaymentRepository {
    override fun save(payment: Payment) {
        this.save(payment)
    }

    override fun findByUuid(uuid: Long): List<Payment> {
        return this.findByUuid(uuid = uuid)
    }
}
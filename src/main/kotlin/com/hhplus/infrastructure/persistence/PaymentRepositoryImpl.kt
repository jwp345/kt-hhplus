package com.hhplus.infrastructure.persistence

import com.hhplus.domain.entity.Payment
import com.hhplus.domain.repository.PaymentRepository
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepositoryImpl : JpaRepository<Payment, Long>, PaymentRepository {
}
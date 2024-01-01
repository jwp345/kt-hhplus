package com.hhplus.domain.repository

import com.hhplus.domain.entity.Payment

interface PaymentRepository {
    fun save(payment: Payment)

    fun findByUuid(uuid : Long) : List<Payment>
}
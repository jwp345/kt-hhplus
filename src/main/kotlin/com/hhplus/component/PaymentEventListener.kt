package com.hhplus.component

import com.hhplus.domain.repository.PaymentRepository
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener


@Component
class PaymentEventListener(val paymentRepository: PaymentRepository) {

    private val log = KotlinLogging.logger("PaymentEventListener")

    @Async
    @TransactionalEventListener
    fun savePayments(paymentEvent: PaymentEvent) {
        try {
            paymentRepository.saveAll(paymentEvent.payments)
        } catch (ex: Exception) {
            log.error { ex.message }
        }
    }
}
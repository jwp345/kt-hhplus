package com.hhplus.component

import com.hhplus.domain.entity.Payment
import com.hhplus.domain.repository.PaymentRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener


@Component
class PaymentEventListener(val paymentRepository: PaymentRepository) {
    /* TODO : saveAll() 메소드가 안먹는 이유 찾기 */
    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun savePayments(paymentEvent: PaymentEvent) {
        for(payment : Payment in paymentEvent.payments) {
            paymentRepository.save(payment)
        }
    }
}
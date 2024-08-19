package com.hhplus.component

import com.hhplus.domain.repository.PaymentRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener


@Component
class PaymentEventListener(val paymentRepository: PaymentRepository) {
    /* TODO : 실패 시 재시도 로직과 로깅 처리 */
    @Async
    @TransactionalEventListener
    fun savePayments(paymentEvent: PaymentEvent) {
        paymentRepository.saveAll(paymentEvent.payments);
    }
}
package com.hhplus.component

import com.hhplus.domain.entity.Payment
import com.hhplus.domain.repository.PaymentRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentEventListener(val paymentRepository: PaymentRepository) {
    /* TODO : saveAll() 메소드가 안먹는 이유 찾기 */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun savePayments(payments: MutableList<Payment>?) {
        if (payments != null) {
            for(payment : Payment in payments) {
                paymentRepository.save(payment)
            }
        }
    }
}
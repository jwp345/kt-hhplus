package com.hhplus.application

import com.hhplus.component.PaymentProcessor
import com.hhplus.domain.entity.Payment
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.infrastructure.security.WaitToken
import com.hhplus.presentation.payment.PaymentCommand
import org.springframework.stereotype.Service

@Service
class PaymentFacade(val paymentProcessor: PaymentProcessor) {

    fun payMoney(concertInfos : List<ConcertInfo>, waitToken: WaitToken): List<Payment> {
        return paymentProcessor.pay(concertInfos = concertInfos, waitToken = waitToken)
    }
}
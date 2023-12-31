package com.hhplus.application

import com.hhplus.component.PaymentProcessor
import com.hhplus.domain.entity.Payment
import com.hhplus.infrastructure.security.WaitToken
import org.springframework.stereotype.Service

@Service
class PaymentFacade(val paymentProcessor: PaymentProcessor) {

    fun payMoney(seatId: Int, bookingDate: String, uuid : Long, waitToken: WaitToken): Payment {
        return paymentProcessor.pay(seatId = seatId, bookingDate = bookingDate, uuid = uuid, waitToken = waitToken)
    }
}
package com.hhplus.application

import com.hhplus.component.PaymentProcessor
import com.hhplus.domain.entity.Payment
import org.springframework.stereotype.Service

@Service
class PaymentFacade(val paymentProcessor: PaymentProcessor) {

    suspend fun payMoney(seatId: Int, bookingDate: String, uuid : Long): Payment {
        return paymentProcessor.pay(seatId = seatId, bookingDate = bookingDate, uuid = uuid)
    }
}
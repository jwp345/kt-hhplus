package com.hhplus.component

import com.hhplus.domain.entity.Payment
import com.hhplus.infrastructure.persistence.PaymentRepositoryImpl
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class PaymentProcessor(val ticketFactory: TicketFactory, val paymentRepository: PaymentRepositoryImpl,
    val userReader: UserReader) {

    suspend fun pay(seatId : Int, uuid : Long, bookingDate : String) : Payment {
        val payment = Payment(uuid = uuid, seatId = seatId, bookingDate = bookingDate,
            price = ticketFactory.getTicket(seatId = seatId, bookingDate= bookingDate,
                user = userReader.read(uuid = uuid)).price)

        coroutineScope {
            launch { paymentRepository.save(payment) }
        }

        return payment
    }
}
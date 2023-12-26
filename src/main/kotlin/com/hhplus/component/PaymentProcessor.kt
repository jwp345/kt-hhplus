package com.hhplus.component

import com.hhplus.domain.entity.Payment
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.domain.repository.WaitOrderRepository
import com.hhplus.infrastructure.persistence.PaymentRepositoryImpl
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class PaymentProcessor(val ticketRepository: TicketRepository, val paymentRepository: PaymentRepositoryImpl,
    val userReader: UserReader, val waitOrderRepository: WaitOrderRepository) {

    suspend fun pay(seatId : Int, uuid : Long, bookingDate : String) : Payment {
        val payment = Payment(uuid = uuid, seatId = seatId, bookingDate = bookingDate,
            price = ticketRepository.getTicketByConcertInfo(seatId = seatId, bookingDate= bookingDate,
                user = userReader.read(uuid = uuid)).price)
        waitOrderRepository.delete(uuid)

        coroutineScope {
            launch { paymentRepository.save(payment) }
        }

        return payment
    }
}
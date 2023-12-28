package com.hhplus.component

import com.hhplus.domain.entity.Payment
import com.hhplus.domain.exception.FailedPaymentException
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.domain.repository.WaitOrderRepository
import com.hhplus.infrastructure.persistence.PaymentRepositoryImpl
import com.hhplus.presentation.booking.BookingStatusCode
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class PaymentProcessor(val ticketRepository: TicketRepository, val paymentRepository: PaymentRepositoryImpl,
    val userReader: UserReader, val waitOrderRepository: WaitOrderRepository, val bookingRepository: BookingRepository) {

    suspend fun pay(seatId : Int, uuid : Long, bookingDate : String) : Payment {
        val tempOrder : Long = waitOrderRepository.findWaitOrderByUuid(uuid = uuid)!!
        try {
            waitOrderRepository.delete(uuid)
            bookingRepository.updateStatusBySeatIdAndBookingDate(
                bookingDate = bookingDate, seatId = seatId, availableCode = BookingStatusCode.CONFIRMED.code
            )
        } catch (e : Exception) {
            waitOrderRepository.save(uuid = uuid, order = tempOrder)
            throw FailedPaymentException()
        }
        val payment = Payment(
            uuid = uuid, seatId = seatId, bookingDate = bookingDate,
            price = ticketRepository.getTicketByConcertInfo(
                seatId = seatId, bookingDate = bookingDate,
                user = userReader.read(uuid = uuid)
            ).price
        )
        coroutineScope {
            launch { paymentRepository.save(payment) }
        }

        return payment
    }
}
package com.hhplus.component

import com.hhplus.domain.entity.Payment
import com.hhplus.domain.info.TicketInfo
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.domain.repository.ValidWaitTokenRepository
import com.hhplus.infrastructure.persistence.PaymentRepositoryImpl
import com.hhplus.infrastructure.security.WaitToken
import com.hhplus.presentation.booking.BookingStatusCode
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class PaymentProcessor(val ticketRepository: TicketRepository, val paymentRepository: PaymentRepositoryImpl,
    val userReader: UserReader, val validWaitTokenRepository: ValidWaitTokenRepository, val bookingRepository: BookingRepository) {

    @Transactional
    fun pay(seatId : Int, uuid : Long, bookingDate : String, waitToken : WaitToken) : Payment {
        userReader.read(uuid = uuid).let { user ->
            val ticketInfo: TicketInfo =
                ticketRepository.getTicket(seatId = seatId, bookingDate = bookingDate, user = user)

            validWaitTokenRepository.pop(waitToken)

            bookingRepository.findBySeatIdAndBookingDateAndStatus(
                bookingDate = bookingDate, seatId = seatId, availableCode = BookingStatusCode.AVAILABLE.code
            )[0].apply {
                status = BookingStatusCode.CONFIRMED.code
            }

            return Payment(
                uuid = uuid, seatId = seatId, bookingDate = bookingDate,
                price = ticketInfo.price
            ).also(paymentRepository::save) // 이벤트 pub 방식으로 변경하자
        }

    }
}
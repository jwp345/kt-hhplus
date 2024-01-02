package com.hhplus.component

import com.hhplus.domain.entity.Payment
import com.hhplus.domain.entity.User
import com.hhplus.domain.exception.FailedFindBookingException
import com.hhplus.domain.exception.FailedPaymentException
import com.hhplus.domain.exception.InvalidTicketException
import com.hhplus.domain.exception.NotEnoughMoneyException
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.domain.repository.ValidWaitTokenRepository
import com.hhplus.infrastructure.security.WaitToken
import com.hhplus.presentation.booking.BookingStatusCode
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentProcessor(val ticketRepository: TicketRepository, val userReader: UserReader,
                       val validWaitTokenRepository: ValidWaitTokenRepository, val bookingRepository: BookingRepository,
    val applicationEventPublisher: ApplicationEventPublisher) {

    @Transactional
    fun pay(concertInfos: List<ConcertInfo>, waitToken : WaitToken) : List<Payment> {
        userReader.read(uuid = waitToken.uuid).let { user ->
            val payments : MutableList<Payment> = mutableListOf()
            var totalPrice : Long = 0
            concertInfos.forEach { concertInfo ->
                checkTicket(concertInfo = concertInfo, user = user)
                try {
                    bookingRepository.findBySeatIdAndBookingDateAndStatus(
                        bookingDate = concertInfo.date,
                        seatId = concertInfo.seatId,
                        availableCode = BookingStatusCode.AVAILABLE.code
                    )[0].apply {
                        status = BookingStatusCode.CONFIRMED.code
                    }.also { booking ->
                        payments.add(Payment(uuid = waitToken.uuid, seatId = concertInfo.seatId, bookingDate = concertInfo.date,
                            price = booking.price))
                        totalPrice += booking.price
                    }
                } catch (e : IndexOutOfBoundsException) {
                    throw FailedFindBookingException()
                } catch (e : Exception) {
                    throw FailedPaymentException()
                }
            }

            checkUserEnoughMoney(price = totalPrice, balance = user.balance)
            user.balance -= totalPrice

            validWaitTokenRepository.pop(waitToken)
            applicationEventPublisher.publishEvent(PaymentEvent(payments = payments))

            return payments
        }
    }

    fun checkUserEnoughMoney(price: Long, balance: Long) {
        if(price > balance) {
            throw NotEnoughMoneyException()
        }
    }

    fun checkTicket(concertInfo: ConcertInfo, user : User) {
        ticketRepository.getLockAndReserveMap().map.let { reserveMap ->
            reserveMap[concertInfo]
                ?: throw InvalidTicketException()
        }
    }
}
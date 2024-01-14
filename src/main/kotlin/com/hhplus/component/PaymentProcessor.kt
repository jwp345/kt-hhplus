package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.entity.Payment
import com.hhplus.domain.entity.User
import com.hhplus.domain.exception.FailedFindBookingException
import com.hhplus.domain.exception.FailedPaymentException
import com.hhplus.domain.exception.NotEnoughMoneyException
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.domain.repository.ValidWaitTokenRepository
import com.hhplus.domain.repository.WaitQueueRepository
import com.hhplus.infrastructure.security.WaitToken
import com.hhplus.presentation.booking.BookingStatusCode
import com.hhplus.presentation.payment.ConcertInfo
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentProcessor(val userReader: UserReader, val waitQueueRepository: WaitQueueRepository,
                       val validWaitTokenRepository: ValidWaitTokenRepository, val bookingRepository: BookingRepository,
    val applicationEventPublisher: ApplicationEventPublisher) {

    private val log = KotlinLogging.logger("PaymentProcessor")
    @Transactional
    fun pay(concertInfos: List<ConcertInfo>, waitToken : WaitToken) : List<Payment> {
        userReader.read(uuid = waitToken.uuid).let { user ->
            val payments : MutableList<Payment> = mutableListOf()
            var totalPrice : Long = 0
            concertInfos.forEach { concertInfo ->
                try {
                    findBooking(concertInfo = concertInfo).apply {
                        status = BookingStatusCode.CONFIRMED.code
                    }.also { booking ->
                        payments.add(Payment(uuid = waitToken.uuid, seatId = concertInfo.seatId, bookingDate = concertInfo.date,
                            price = booking.price))
                        totalPrice += booking.price
                    }
                } catch (e : OptimisticLockingFailureException) {
                    throw FailedFindBookingException()
                }
            }.also {
                payTotals(user = user, totalPrice = totalPrice, waitToken = waitToken, payments = payments)
            }

            return payments
        }
    }

    private fun findBooking(concertInfo: ConcertInfo) : Booking {
        return bookingRepository.findBySeatIdAndBookingDateAndStatus(
            bookingDate = concertInfo.date,
            seatId = concertInfo.seatId,
            availableCode = BookingStatusCode.RESERVED.code
        ).firstOrNull() ?: throw FailedFindBookingException()
    }

    private fun payTotals(user : User, totalPrice : Long, waitToken: WaitToken, payments : MutableList<Payment>) {
        checkUserEnoughMoney(price = totalPrice, balance = user.balance)
        user.balance -= totalPrice

        validWaitTokenRepository.remove(token = waitToken)
        validWaitTokenRepository.add(token = waitQueueRepository.pop())
        applicationEventPublisher.publishEvent(PaymentEvent(payments = payments))
    }

    fun checkUserEnoughMoney(price: Long, balance: Long) {
        if(price > balance) {
            throw NotEnoughMoneyException()
        }
    }
}
package com.hhplus.component

import com.hhplus.domain.entity.User
import com.hhplus.domain.exception.FailedFindBookingException
import com.hhplus.domain.exception.FailedPaymentException
import com.hhplus.domain.exception.InvalidTicketException
import com.hhplus.domain.exception.NotEnoughMoneyException
import com.hhplus.domain.info.AssignmentInfo
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.domain.repository.ValidWaitTokenRepository
import com.hhplus.infrastructure.security.WaitToken
import com.hhplus.presentation.booking.BookingStatusCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDateTime

internal class PaymentProcessorTest : AnnotationSpec(){
    private val ticketRepository : TicketRepository = mockk<TicketRepository>()
    private val userReader : UserReader = mockk<UserReader>()
    private val validWaitTokenRepository : ValidWaitTokenRepository = mockk<ValidWaitTokenRepository>()
    private val bookingRepository : BookingRepository = mockk<BookingRepository>()
    private val applicationEventPublisher = mockk<ApplicationEventPublisher>()
    private val paymentProcessor = PaymentProcessor(
            ticketRepository = ticketRepository,
            userReader = userReader,
            bookingRepository = bookingRepository,
        validWaitTokenRepository = validWaitTokenRepository,
        applicationEventPublisher = applicationEventPublisher
        )

    @Test
    fun `유효한 예약 정보가 없다면 예외를 발생`() {
        val concertInfo = ConcertInfo(seatId = 1, date = "2023-11-20 11:20")
        every { ticketRepository.getLockAndReserveMap().map[concertInfo] } returns null
        shouldThrow<InvalidTicketException> {
            paymentProcessor.checkTicket(concertInfo = concertInfo, user = User(name = "name", balance = 1000L)) }
    }

    @Test
    fun `잔액이 부족할 경우 예외가 발생`() {
        shouldThrow<NotEnoughMoneyException> {
            paymentProcessor.checkUserEnoughMoney(price = 5000, balance = 100)
        }
    }

    @Test
    fun `예약 정보는 있지만 이미 결제가 완료됐을 경우거나 조회가 안됐을 때 예외 발생`() {
        val user = User(name = "name", balance = 1000)
        val concertInfo = ConcertInfo(seatId = 1, date = "2023-11-20 11:20")
        every { userReader.read(1) }.returns(user)
        every { ticketRepository.getLockAndReserveMap().map[concertInfo] } returns AssignmentInfo(uuid = 1, createdAt = LocalDateTime.now())
        every { bookingRepository.findBySeatIdAndBookingDateAndStatus(bookingDate = concertInfo.date,
            seatId = concertInfo.seatId, availableCode = BookingStatusCode.AVAILABLE.code) }.returns(listOf())

        shouldThrow<FailedFindBookingException> {
            paymentProcessor.pay(concertInfos = listOf(concertInfo), waitToken = WaitToken(uuid = 1, order = 1, createAt = LocalDateTime.now()))
        }
    }

    @Test
    fun `db 조회 중 커넥션 에러가 발생 시 결제 실패 예외 발생`() {
        val user = User(name = "name", balance = 1000)
        val concertInfo = ConcertInfo(seatId = 1, date = "2023-11-20 11:20")
        every { userReader.read(1) }.returns(user)
        every { ticketRepository.getLockAndReserveMap().map[concertInfo] } returns AssignmentInfo(uuid = 1, createdAt = LocalDateTime.now())
        every { bookingRepository.findBySeatIdAndBookingDateAndStatus(bookingDate = concertInfo.date,
            seatId = concertInfo.seatId, availableCode = BookingStatusCode.AVAILABLE.code) }.throws(RuntimeException())

        shouldThrow<FailedPaymentException> {
            paymentProcessor.pay(concertInfos = listOf(concertInfo), waitToken = WaitToken(uuid = 1, order = 1, createAt = LocalDateTime.now()))
        }
    }
}
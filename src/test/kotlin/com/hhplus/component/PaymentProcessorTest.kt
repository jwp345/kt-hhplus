package com.hhplus.component

import com.hhplus.domain.exception.FailedPaymentException
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.domain.repository.WaitOrderRepository
import com.hhplus.infrastructure.persistence.PaymentRepositoryImpl
import com.hhplus.presentation.booking.BookingStatusCode
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.assertThrows


@ExperimentalCoroutinesApi
internal class PaymentProcessorTest : AnnotationSpec(){
    private val ticketRepository = mockk<TicketRepository>()
    private val paymentRepository = mockk<PaymentRepositoryImpl>()
    private val userReader = mockk<UserReader>()
    private val waitOrderRepository = mockk<WaitOrderRepository>()
    private val bookingRepository = mockk<BookingRepository>()

    private val paymentProcessor = PaymentProcessor(
            ticketRepository = ticketRepository,
            paymentRepository = paymentRepository,
            userReader = userReader,
            waitOrderRepository = waitOrderRepository,
            bookingRepository = bookingRepository
        )


    @Test
    suspend fun `보상 트랜잭션 테스트`() {
        // Given
        val seatId = 1
        val uuid = 123L
        val bookingDate = "2023-01-01 10:00"
        val tempOrder = 456L
        val exceptionMessage = "Simulate an exception"

        // Stubbing
        coEvery { waitOrderRepository.findWaitOrderByUuid(uuid) } returns tempOrder
        coEvery { waitOrderRepository.delete(uuid) } throws RuntimeException(exceptionMessage)
        coEvery {
            bookingRepository.updateStatusBySeatIdAndBookingDate(
                bookingDate = bookingDate,
                seatId = seatId,
                availableCode = BookingStatusCode.CONFIRMED.code
            )
        } throws RuntimeException(exceptionMessage)
        coEvery { waitOrderRepository.save(uuid, tempOrder) } returns Unit

        // When & Then
        assertThrows<FailedPaymentException> {
            paymentProcessor.pay(seatId, uuid, bookingDate)
        }

        // Verify
        verify(exactly = 1) { waitOrderRepository.delete(uuid) }
        verify(exactly = 1) {
            bookingRepository.updateStatusBySeatIdAndBookingDate(
                bookingDate = bookingDate,
                seatId = seatId,
                availableCode = BookingStatusCode.CONFIRMED.code
            )
        }
        verify(exactly = 1) { waitOrderRepository.save(uuid, tempOrder) }
        verify(exactly = 0) { paymentRepository.save(any()) }
    }
}
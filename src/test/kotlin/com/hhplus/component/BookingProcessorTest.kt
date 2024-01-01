package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.exception.FailedReserveException
import com.hhplus.domain.exception.InvalidTicketException
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.presentation.booking.BookingStatusCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given

import org.mockito.Mockito.mock
import org.redisson.client.RedisTimeoutException

internal class BookingProcessorTest {

    private val ticketRepository: TicketRepository = mock(TicketRepository::class.java)
    private val bookingProcessor = BookingProcessor(ticketRepository)

    @Test
    fun `예약 정보가 0개나 1개 보다 많이 조회되면 예외를 출력한다`() {
        assertThrows(InvalidTicketException::class.java) {
            bookingProcessor.reserve(
                bookings = listOf(
                    Booking(
                        seatId = 1,
                        bookingDate = "date",
                        status = BookingStatusCode.AVAILABLE,
                        price = 100L
                    ), Booking(seatId = 2,
                        bookingDate = "date",
                        status = BookingStatusCode.AVAILABLE,
                        price = 100L)
                ), uuid = 1L
            )
        }

        assertThrows(InvalidTicketException::class.java) {
            bookingProcessor.reserve(
                bookings = listOf(), uuid = 1L
            )
        }
    }

    @Test
    fun `예약 시도 중 레디스에서 예외 발생하면 예약 취소 예외 발생`() {
        val date = "20231120"
        val seatId = 1
        val uuid = 1L
        val price = 5000L

        given(ticketRepository.getLockAndReserveMap()).willThrow(RedisTimeoutException())

        assertThrows(FailedReserveException::class.java) {
            bookingProcessor.reserve(listOf(Booking(
                seatId = seatId,
                bookingDate = date,
                status = BookingStatusCode.AVAILABLE,
                price = price
            )), uuid = uuid)
        }
    }

    @Test
    fun `이미 예약이 되어있다면 예약 실패 예외 발생`() {
        assertThrows(FailedReserveException::class.java) {
            bookingProcessor.reserve(
                bookings = listOf(
                    Booking(
                        seatId = 1,
                        bookingDate = "date",
                        status = BookingStatusCode.AVAILABLE,
                        price = 100L
                    )
                ), uuid = 1
            )
        }
    }
}
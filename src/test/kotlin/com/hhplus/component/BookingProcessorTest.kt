package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.exception.InvalidTicketException
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.presentation.booking.BookingStatusCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

import org.mockito.Mockito.mock

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
}
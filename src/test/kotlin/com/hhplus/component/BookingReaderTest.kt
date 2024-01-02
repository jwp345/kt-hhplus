package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.presentation.booking.BookingStatusCode
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class BookingReaderTest {

    private val bookingRepository : BookingRepository = mockk<BookingRepository>()

    private val ticketRepository : TicketRepository = mockk<TicketRepository>()

    private val bookingReader: BookingReader = BookingReader(bookingRepository, ticketRepository)

    private val seatId : Int = 1
    private val bookingDateOne : String = "20231101"
    private val bookingDateSec : String = "20231102"

    @BeforeEach
    fun stubbing() {
        val bookingList = listOf(
            Booking(seatId = seatId, bookingDate = bookingDateOne, status = BookingStatusCode.AVAILABLE, price = 5000L),
            Booking(seatId = seatId, bookingDate = bookingDateSec, status = BookingStatusCode.AVAILABLE, price = 1000L)
        )

        every { bookingRepository.findBySeatIdAndStatus(seatId = seatId, availableCode = BookingStatusCode.AVAILABLE.code) } returns bookingList
    }

    @Test
    fun `가능한 날짜가 있으면 반환한다`() {
        every { ticketRepository.getLockAndReserveMap().map.contains(any()) } returns false
        assertThat(bookingReader.read(seatId = seatId).size).isEqualTo(2)
    }

    @Test
    fun `예약이 있을 경우 리스트에서 뺀다`() {
        every { ticketRepository.getLockAndReserveMap().map.contains(ConcertInfo(seatId = seatId, date = bookingDateOne)) } returns false
        every { ticketRepository.getLockAndReserveMap().map.contains(ConcertInfo(seatId = seatId, date = bookingDateSec)) } returns true
        assertThat(bookingReader.read(seatId = seatId).size).isEqualTo(1)
    }
}
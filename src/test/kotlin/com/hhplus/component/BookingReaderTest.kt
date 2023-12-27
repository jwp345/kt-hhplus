package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.repository.BookingRepository
import com.hhplus.domain.repository.TicketRepository
import com.hhplus.presentation.booking.BookingStatusCode
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

internal class BookingReaderTest{

    private val bookingRepository : BookingRepository = mockk<BookingRepository>()

    private val ticketRepository : TicketRepository = mockk<TicketRepository>()

    private val bookingReader: BookingReader = BookingReader(bookingRepository, ticketRepository)

    private val seatId : Int = 1
    private val bookingDate : String = "20231101"

    @BeforeEach
    fun stubbing() {
        val bookingList = listOf(
            Booking(seatId = seatId, bookingDate = bookingDate, status = BookingStatusCode.AVAILABLE, price = 5000L),
            Booking(seatId = seatId + 1, bookingDate = bookingDate, status = BookingStatusCode.AVAILABLE, price = 1000L)
        )

        every { bookingRepository.getDatesBySeatId(seatId, BookingStatusCode.AVAILABLE) } returns listOf(bookingDate)
    }

    @Test
    fun `가능한 날짜가 있으면 반환한다`() {
        given(ticketRepository.getLockAndReserveMap().mapCache.contains(any(ConcertInfo::class.java)))
            .willReturn(true)

        assertThat(bookingReader.read(seatId = seatId).size).isEqualTo(1)
    }


}
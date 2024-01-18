package com.hhplus.component

import com.hhplus.domain.entity.Booking
import com.hhplus.domain.exception.FailedReserveException
import com.hhplus.presentation.booking.BookingStatusCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given

import org.mockito.Mockito.mock

internal class BookingProcessorTest {

    private val bookingReader: BookingReader = mock(BookingReader::class.java)
    private val userReader: UserReader = mock(UserReader::class.java)
    private val bookingProcessor = BookingProcessor(bookingReader, userReader = userReader)

    val seatId : Int = 1
    val bookingDate : String = "2023-11-20 11:15"

    @Test
    fun `예약 정보가 0개나 1개 보다 많이 조회되면 예외를 출력한다`() {
        given(bookingReader.read(seatId = seatId, bookingDate = bookingDate)).willReturn(listOf(
            Booking(
                seatId = 1,
                bookingDate = "date",
                status = BookingStatusCode.AVAILABLE,
                price = 100L
            ), Booking(seatId = 2,
                bookingDate = "date",
                status = BookingStatusCode.AVAILABLE,
                price = 100L)
        ))
        given(userReader.read(uuid = 1)).willReturn(null)
        assertThrows(FailedReserveException::class.java) {
            bookingProcessor.reserve(seatId = seatId, bookingDate = bookingDate, uuid = 1)
        }

        assertThrows(FailedReserveException::class.java) {
            bookingProcessor.reserve(seatId = 22, bookingDate = "hi", uuid = 1)
        }
    }
}
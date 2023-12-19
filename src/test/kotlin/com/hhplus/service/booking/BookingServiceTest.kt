package com.hhplus.service.booking

import com.hhplus.common.BookingStatusCode
import com.hhplus.exception.AlreadyReservationException
import com.hhplus.exception.InvalidTicketException
import com.hhplus.model.Booking
import com.hhplus.repository.booking.BookingRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BookingServiceTest {

    @Autowired
    private lateinit var bookingService: BookingService

    @Autowired
    private lateinit var bookingRepository: BookingRepository

    @BeforeAll
    internal fun init() {
        bookingRepository.save(Booking(seatId = 1, bookingDate = "2023-12-13 15:30", status = BookingStatusCode.AVAILABLE))
        bookingRepository.save(Booking(seatId = 1, bookingDate = "2023-12-13 17:30",
            status = BookingStatusCode.CONFIRMED))
    }

    @Test
    fun `1~50 사이의 숫자 아닌 인풋 테스트`() {
        assertThrows(IllegalArgumentException::class.java) {
            bookingService.findDatesAvailable(51)
        }
        assertThrows(IllegalArgumentException::class.java) {
            bookingService.findDatesAvailable(0)
        }
    }

    @Test
    fun `유효한 날짜 아닌 인풋 테스트`() {
        assertThrows(IllegalArgumentException::class.java) {
            bookingService.findSeatsAvailable("12")
        }
    }

    @Test
    fun `예약 시 유효한 쿠폰의 정보 아닐 시 오류를 발생`() {
        assertThrows(InvalidTicketException::class.java) {
            bookingService.reserveSeat(seatId = 1, bookingDate = "2023-12-13 16:30", userId = 1)
        }
    }

    @Test
    fun `예약 시 이미 예약이 되어 있다면 오류를 발생`() {
        assertThrows(AlreadyReservationException::class.java) {
            bookingService.reserveSeat(seatId = 1, bookingDate = "2023-12-13 15:30", userId = 1)
            bookingService.reserveSeat(seatId = 1, bookingDate = "2023-12-13 15:30", userId = 1)
        }
    }

    @Test
    fun `예약을 하게 되면 예약이 되어 있는 좌석은 예약 가능 리스트에 없다`() {
        bookingService.reserveSeat(seatId = 1, bookingDate = "2023-12-13 15:30", userId = 1)

    }

}
package com.hhplus.service.booking

import com.hhplus.common.BookingStatusCode
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
import java.time.LocalDateTime

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BookingServiceTest {

    @Autowired
    private lateinit var bookingService: BookingService

    @Autowired
    private lateinit var bookingRepository: BookingRepository

    @BeforeAll
    internal fun init() {
        bookingRepository.save(Booking(seatId = 1L, bookingDate = "2023-12-13 15:30", status = BookingStatusCode.AVAILABLE))
        bookingRepository.save(Booking(seatId = 1L, bookingDate = "2023-12-13 17:30",
            status = BookingStatusCode.RESERVED, reservedDate = LocalDateTime.now().minusDays(6)))
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
    fun `유효 데이터 테스트`() {
        assertThat(bookingService.findDatesAvailable(1).dates.size).isEqualTo(2)
        assertThat(bookingService.findSeatsAvailable("2023-12-13 17:30").seats.size).isEqualTo(1)
        assertThat(bookingService.findSeatsAvailable("2023-12-13 15:30").seats.size).isEqualTo(1)
    }

}
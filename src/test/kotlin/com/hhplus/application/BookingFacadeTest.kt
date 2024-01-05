package com.hhplus.application

import com.hhplus.component.BookingReader
import com.hhplus.presentation.booking.BookingStatusCode
import com.hhplus.domain.entity.Booking
import com.hhplus.domain.exception.*
import com.hhplus.domain.repository.BookingRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class BookingFacadeTest {

    @Autowired
    private lateinit var bookingFacade: BookingFacade

    @Autowired
    private lateinit var bookingRepository: BookingRepository


    @BeforeAll
    internal fun init() {
        bookingRepository.save(
            Booking(seatId = 1, bookingDate = "2023-12-13 15:30", status = BookingStatusCode.AVAILABLE,
            price = 3000)
        )
        bookingRepository.save(
            Booking(seatId = 1, bookingDate = "2023-12-13 16:30", status = BookingStatusCode.AVAILABLE,
                price = 3000)
        )
        bookingFacade.reserveSeat(seatId = 1, bookingDate = "2023-12-13 16:30", uuid = 1)
        bookingRepository.save(
            Booking(seatId = 1, bookingDate = "2023-12-13 17:30",
            status = BookingStatusCode.CONFIRMED, price = 5000)
        )
        bookingRepository.save(
            Booking(seatId = 2, bookingDate = "2023-12-13 15:30", status = BookingStatusCode.AVAILABLE,
                price = 3000)
        )
    }

    @Test
    fun `1~50 사이의 숫자 아닌 인풋 테스트`() {
        assertThrows(InvalidSeatIdException::class.java) {
            bookingFacade.findDatesAvailable(51)
        }
        assertThrows(InvalidSeatIdException::class.java) {
            bookingFacade.findDatesAvailable(0)
        }
    }

    @Test
    fun `유효한 날짜 아닌 인풋 테스트`() {
        assertThrows(InvalidBookingDateException::class.java) {
            bookingFacade.findSeatsAvailable("12")
        }
    }

    @Test
    fun `예약 시 유효한 쿠폰의 정보 아닐 시 오류를 발생`() {
        assertThrows(FailedReserveException::class.java) {
            bookingFacade.reserveSeat(seatId = 1, bookingDate = "2023-12-13 19:30", uuid = 1L)
        }
    }

    @Test
    fun `예약 시 이미 예약이 되어 있다면 오류를 발생`() {
        assertThrows(FailedReserveException::class.java) {
            bookingFacade.reserveSeat(seatId = 1, bookingDate = "2023-12-13 15:30", uuid = 1)
            bookingFacade.reserveSeat(seatId = 1, bookingDate = "2023-12-13 15:30", uuid = 1)
        }
    }

    @Test
    fun `예약을 하게 되면 예약이 되어 있는 좌석은 예약 가능 리스트에 없다`() {
        try {
            bookingFacade.reserveSeat(seatId = 1, bookingDate = "2023-12-13 15:30", uuid = 1)
        } catch (_: Exception) {}
        assertThat(bookingFacade.findSeatsAvailable("2023-12-13 15:30").size).isZero()
        assertThat(bookingFacade.findDatesAvailable(1).size).isZero()
    }

    @Test
    @Order(2)
    fun `예약이 가능하면 예약을 성공한다`() {
        bookingFacade.reserveSeat(seatId = 1, bookingDate = "2023-12-13 15:30", uuid = 1)
        assertThat(bookingRepository.findBySeatIdAndBookingDateAndStatus(seatId = 1,
            bookingDate = "2023-12-13 15:30", availableCode = BookingStatusCode.RESERVED.code).size).isEqualTo(1)
    }

    @Test
    @Order(1)
    fun `예약 가능한 날짜 조회시 조회 된다`() {
        assertThat(bookingFacade.findDatesAvailable(seatId = 1).size).isEqualTo(1)
    }

    @Test
    fun `예약 가능한 좌석 조회 시 조회 된다`() {
        assertThat(bookingFacade.findSeatsAvailable(date = "2023-12-13 16:30").size).isEqualTo(0)
    }

    @Test
    fun `동시에 10개 예약 요청이 오면 하나의 요청 빼곤 실패해야 한다`() {
        val executor = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(10)

        val exceptionNum = AtomicInteger(0)

        repeat(10) {
            executor.submit{
                try {
                    bookingFacade.reserveSeat(seatId = 2, bookingDate = "2023-12-13 15:30", uuid = 1L)
                } catch (_: Exception) {
                    exceptionNum.getAndIncrement()
                }
                latch.countDown()
            }
        }

        latch.await()
        executor.shutdown()
        assertThat(exceptionNum.get()).isEqualTo(9)
    }
}
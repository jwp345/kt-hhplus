package com.hhplus.service.booking

import com.hhplus.common.BookingStatusCode
import com.hhplus.exception.AlreadyReservationException
import com.hhplus.exception.InvalidTicketException
import com.hhplus.model.booking.Booking
import com.hhplus.repository.booking.BookingRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BookingServiceTest {

    @Autowired
    private lateinit var bookingService: BookingService

    @Autowired
    private lateinit var bookingRepository: BookingRepository

    @Autowired
    private lateinit var redissonClient: RedissonClient

    @BeforeAll
    internal fun init() {
        redissonClient.getMapCache<String, Long>("seatReservation").clear()
        bookingRepository.save(
            Booking(seatId = 1, concertDate = "2023-12-13 15:30", status = BookingStatusCode.AVAILABLE,
            price = 3000)
        )
        bookingRepository.save(
            Booking(seatId = 1, concertDate = "2023-12-13 17:30",
            status = BookingStatusCode.CONFIRMED, price = 5000)
        )
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
            bookingService.reserveSeat(seatId = 1, concertDate = "2023-12-13 16:30", userId = 1)
        }
    }

    @Test
    fun `예약 시 이미 예약이 되어 있다면 오류를 발생`() {
        assertThrows(AlreadyReservationException::class.java) {
            bookingService.reserveSeat(seatId = 1, concertDate = "2023-12-13 15:30", userId = 1)
            bookingService.reserveSeat(seatId = 1, concertDate = "2023-12-13 15:30", userId = 1)
        }
    }

    @Test
    fun `예약을 하게 되면 예약이 되어 있는 좌석은 예약 가능 리스트에 없다`() {
        try {
            bookingService.reserveSeat(seatId = 1, concertDate = "2023-12-13 15:30", userId = 1)
        } catch (_: Exception) {}
        assertThat(bookingService.findSeatsAvailable("2023-12-13 15:30").seats.size).isZero()
        assertThat(bookingService.findDatesAvailable(1).dates.size).isZero()
    }

    @Test
    fun `동시에 10개 예약 요청이 오면 하나의 요청 빼곤 실패해야 한다`() {
        val executor = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(10)

        val exceptionNum = AtomicInteger(0)

        repeat(10) {
            executor.submit{
                try {
                    bookingService.reserveSeat(1, concertDate = "2023-12-13 15:30", userId = 1)
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
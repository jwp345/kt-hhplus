package com.hhplus.service.booking

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
internal class BookingServiceTest {
    @Autowired
    private lateinit var bookingService: BookingService

    @Test
    fun `1~50 사이의 숫자 아닌 인풋 테스트`() {
        assertThrows(IllegalArgumentException::class.java) {
            bookingService.findDatesAvailable(51)
        }
        assertThrows(IllegalArgumentException::class.java) {
            bookingService.findDatesAvailable(0)
        }
    }

}
package com.hhplus.component

import com.hhplus.domain.repository.BookingRepository
import org.assertj.core.api.BDDAssumptions.given
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired

internal class BookingProcessorTest {

    @Autowired
    lateinit var bookingRepository : BookingRepository

    @BeforeEach
    fun stubbing() {
        given(bookingRepository\(any(String::class.java))).willReturn(false)
    }
}
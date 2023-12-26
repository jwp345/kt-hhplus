package com.hhplus.application

import com.hhplus.component.BookingProcessor
import com.hhplus.component.BookingReader
import com.hhplus.domain.entity.Booking
import org.springframework.stereotype.Service

@Service
class BookingFacade (val bookingReader: BookingReader, val bookingProcessor: BookingProcessor) {

    /* TODO : 캐시(예약) 이력을 어떻게 쌓을 것인가?
        * 일정 시간마다 스케줄러 돌려서 배치성으로 실행해야 하나?
        * OR 이벤트 방식으로 처리하여 비동기로 실행시킬까?
    * */

    fun findDatesAvailable(seatId : Int) : List<Booking> {
        return bookingReader.read(seatId = seatId)
    }

    fun findSeatsAvailable(date: String): List<Booking> {
        return bookingReader.read(date)
    }

    fun reserveSeat(seatId: Int, bookingDate: String, uuid: Long): Booking {
        return bookingReader.read(seatId = seatId, bookingDate = bookingDate)
            .let { bookings -> bookingProcessor.reserve(bookings = bookings, uuid = uuid)
            }
    }
}


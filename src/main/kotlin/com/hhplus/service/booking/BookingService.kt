package com.hhplus.service.booking

import com.hhplus.common.BookingStatusCode
import com.hhplus.common.InvalidDateException
import com.hhplus.common.InvalidSeatIdException
import com.hhplus.controller.booking.DatesAvailableDto
import com.hhplus.controller.booking.ReservationDto
import com.hhplus.controller.booking.SeatsAvailableDto
import com.hhplus.repository.booking.BookingRepository
import org.redisson.api.RMap
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class BookingService (val bookingRepository: BookingRepository) {

    /* TODO : 캐시 리팩토링
    * 시나리오 : 예약 시 5분간 TTL 걸어놓고 삭제한다.
    * 캐시 히트 시 반환, 미스 시 db 저장
    * 추후에 redis로 리팩토링 할 것, 지금은 caffeine 라이브러리로 해결할까? 근데 그러면 어플리케이션 종료 시 데이터도 같이 날라가지않나?
    * redis를 사용할 경우, redis가 죽을 경우도 대비하여 캐시 미스 시 db 조회까지 해야할까?
    * */

    private val expiredBookingTime = LocalDateTime.now().minusDays(5)

    fun findDatesAvailable(seatId : Int) : DatesAvailableDto {
        checkSeatId(seatId = seatId)
        return DatesAvailableDto(bookingRepository.findDatesBySeatId(seatId = seatId, targetDate = expiredBookingTime,
            availableCode = BookingStatusCode.AVAILABLE.code, reservedCode = BookingStatusCode.RESERVED.code))
    }

    fun findSeatsAvailable(date: String): SeatsAvailableDto {
        checkBookingDate(date = date)
        return SeatsAvailableDto(bookingRepository.findSeatIdByBookingDate(bookingDate = date, targetDate = expiredBookingTime,
            availableCode = BookingStatusCode.AVAILABLE.code, reservedCode = BookingStatusCode.RESERVED.code))
    }

    fun reserveSeat(seatId: Int, bookingDate: String): ReservationDto {
        checkSeatId(seatId = seatId)
        checkBookingDate(date = bookingDate)
        RMap
        return ReservationDto()
    }

    fun checkBookingDate(date: String): LocalDateTime {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        } catch (e: Exception) {
            throw InvalidDateException()
        }
    }

    fun checkSeatId(seatId: Int) {
        if (seatId > 50 || seatId <= 0) {
            throw InvalidSeatIdException()
        }
    }

}


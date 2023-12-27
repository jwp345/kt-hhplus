package com.hhplus.domain.repository

import com.hhplus.domain.entity.User
import com.hhplus.domain.info.ConcertInfo
import com.hhplus.domain.info.ReserveCacheInfo
import com.hhplus.domain.info.TicketInfo
import java.util.concurrent.TimeUnit

interface TicketRepository {
    fun getLockAndReserveMap() : ReserveCacheInfo
    fun getTicketByConcertInfo(seatId : Int, bookingDate : String, user : User) : TicketInfo
    fun saveReserveMap(concertInfo: ConcertInfo, ticketInfo: TicketInfo, ttl: Long, timeUnit: TimeUnit)
}
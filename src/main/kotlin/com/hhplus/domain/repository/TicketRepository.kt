package com.hhplus.domain.repository

import com.hhplus.domain.entity.User
import com.hhplus.domain.info.ReserveCacheInfo
import com.hhplus.domain.info.TicketInfo

interface TicketRepository {
    fun getLockAndReserveMap() : ReserveCacheInfo
    fun getTicketByConcertInfo(seatId : Int, bookingDate : String, user : User) : TicketInfo
}
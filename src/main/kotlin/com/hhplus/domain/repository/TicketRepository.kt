package com.hhplus.domain.repository

import com.hhplus.domain.info.ReserveCacheInfo

interface TicketRepository {
    fun getLockAndReserveMap() : ReserveCacheInfo
}
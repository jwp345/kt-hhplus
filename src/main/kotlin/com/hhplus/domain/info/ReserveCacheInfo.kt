package com.hhplus.domain.info

import org.redisson.api.RLock
import org.redisson.api.RMapCache

// Redis 조회용
data class ReserveCacheInfo (val mapCache : RMapCache<ConcertInfo, TicketInfo>, val lock : RLock){
}
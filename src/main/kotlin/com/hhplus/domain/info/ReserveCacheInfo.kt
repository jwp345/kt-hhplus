package com.hhplus.domain.info

import org.redisson.api.RLock
import org.redisson.api.RMapCache

// Redis 조회용 value domain
data class ReserveCacheInfo (val mapCache : RMapCache<ConcertInfo, TicketInfo>, val lock : RLock){
}
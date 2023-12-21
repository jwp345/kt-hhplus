package com.hhplus.component

import org.redisson.api.RLock
import org.redisson.api.RMapCache

data class ReserveCacheInfo (val mapCache : RMapCache<ConcertInfo, TicketInfo>, val lock : RLock){
}
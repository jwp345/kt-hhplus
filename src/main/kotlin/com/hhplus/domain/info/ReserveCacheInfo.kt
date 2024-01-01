package com.hhplus.domain.info

import org.redisson.api.RLock
import org.redisson.api.RMap

// Redis 조회용
data class ReserveCacheInfo (val map : RMap<ConcertInfo, AssignmentInfo>, val lock : RLock){
}
package com.hhplus.domain.repository


interface WaitOrderRepository {
    fun getWaitOrder() : Long

    fun findWaitOrderByUuid(uuid : Long) : Long?

    fun save(uuid: Long, order : Long)
}
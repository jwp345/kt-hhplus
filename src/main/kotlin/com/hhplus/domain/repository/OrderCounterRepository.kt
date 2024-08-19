package com.hhplus.domain.repository


interface OrderCounterRepository {

    fun incrementAndGet() : Long
}
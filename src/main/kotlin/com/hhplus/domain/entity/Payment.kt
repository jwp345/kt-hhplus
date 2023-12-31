package com.hhplus.domain.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "payment")
final class Payment(uuid: Long, seatId: Int, bookingDate: String, price : Long) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long ?= null

    var uuid : Long = uuid

    @Column(name = "seat_id",  nullable = false, length = 2)
    var seatId : Int = seatId

    @Column(name = "booking_date", nullable = false, length = 16)
    var bookingDate : String = bookingDate

    @Column(name = "price", nullable = false) // 단위 : 원
    var price : Long = price

    @Column(name = "payment_date")
    var paymentDate : LocalDateTime  = LocalDateTime.now()
}
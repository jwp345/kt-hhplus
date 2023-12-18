package com.hhplus.model

import com.hhplus.common.BaseEntity
import com.hhplus.common.BookingStatusCode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table
class Booking(seatId : Long, bookingDate : String, status : BookingStatusCode,
              userId: Long ?= null, reservedDate: LocalDateTime ?= null) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "seat_id", length = 2)
    var seatId : Long = seatId

    @Column(name = "booking_date", length = 16)
    var bookingDate : String = bookingDate

    @Column(name = "status", nullable = false, length = 1)
    var status : Int = status.code

    @Column(name = "user_id", nullable = true)
    var userId : Long? = userId

    @Column(name = "reserved_at", nullable = true)
    var reservedAt : LocalDateTime? = reservedDate
}
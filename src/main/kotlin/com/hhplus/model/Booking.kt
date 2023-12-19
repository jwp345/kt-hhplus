package com.hhplus.model

import com.hhplus.common.BaseEntity
import com.hhplus.common.BookingStatusCode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
class Booking(seatId : Int, bookingDate : String, status : BookingStatusCode) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "seat_id", length = 2)
    var seatId : Int = seatId

    @Column(name = "booking_date", length = 16)
    var bookingDate : String = bookingDate

    @Column(name = "status", nullable = false, length = 1)
    var status : Int = status.code
}
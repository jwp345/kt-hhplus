package com.hhplus.domain.entity

import com.hhplus.presentation.booking.BookingStatusCode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Version

@Entity
@Table(name = "booking", uniqueConstraints = [
    UniqueConstraint(name = "UniqueSeatIdAndConcertDate", columnNames = ["seat_id","booking_date"])
])
final class Booking(seatId : Int, bookingDate : String, status : BookingStatusCode, price : Long) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "seat_id",  nullable = false, length = 2)
    var seatId : Int = seatId

    @Column(name = "booking_date", nullable = false, length = 16)
    var bookingDate : String = bookingDate

    @Column(name = "status", nullable = false, length = 1)
    var status : Int = status.code

    @Column(name = "price", nullable = false) // 단위 : 원
    var price : Long = price

    @Version
    private val version : Int ?= null
}
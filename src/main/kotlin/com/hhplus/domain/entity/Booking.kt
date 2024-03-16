package com.hhplus.domain.entity

import com.hhplus.presentation.booking.BookingStatusCode
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "booking", uniqueConstraints = [
    UniqueConstraint(name = "UniqueSeatIdAndConcertDate", columnNames = ["seat_id","booking_date"])
], indexes = [Index(name = "idx_bookingDate", columnList = "booking_date")]
)
final class Booking(seatId : Int, bookingDate : LocalDateTime, status : BookingStatusCode, price : Long) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "seat_id",  nullable = false, length = 2)
    var seatId : Int = seatId

    @Column(name = "booking_date", nullable = false)
    var bookingDate : LocalDateTime = bookingDate

    @Column(name = "status", nullable = false, length = 1)
    var status : Int = status.code

    @Column(name = "price", nullable = false) // 단위 : 원
    var price : Long = price

    @Column(name = "user_uuid", nullable = true)
    var userUuid : Long ?= null

    @Version
    private val version : Long ?= null
}
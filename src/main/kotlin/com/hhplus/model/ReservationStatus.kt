package com.hhplus.model

import com.hhplus.common.BaseEntity
import com.hhplus.common.ReservationStatusCode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class ReservationStatus(seatId : Long, reserveDate : LocalDateTime, reservationStatusCode: ReservationStatusCode) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "seat_id")
    var seatId : Long = seatId

    @Column(name = "reserve_date")
    var reserveDate : LocalDateTime = reserveDate

    @Column(name = "status")
    var status : ReservationStatusCode = reservationStatusCode
}
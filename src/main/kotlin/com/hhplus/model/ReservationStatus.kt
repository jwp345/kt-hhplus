package com.hhplus.model

import com.hhplus.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "seat_reservation_status")
class ReservationStatus(seatId : Long, availableDate : LocalDateTime, status : Int) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "SEAT_ID")
    var seatId : Long = seatId

    @Column(name = "AVAILABLE_DATE")
    var availableDate : LocalDateTime = availableDate

    @Column(name = "STATUS")
    var status : Int = status
}
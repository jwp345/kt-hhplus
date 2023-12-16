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
@Table
class Booking(seatId : Long, availableDate : LocalDateTime, status : Int, userId: Long? = null) : BaseEntity(){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "SEAT_ID", length = 2)
    var seatId : Long = seatId

    @Column(name = "AVAILABLE_DATE")
    var availableDate : LocalDateTime = availableDate

    @Column(name = "STATUS", nullable = false, length = 1)
    var status : Int = status

    @Column(name = "USER_ID", nullable = true)
    var userId : Long? = userId
}
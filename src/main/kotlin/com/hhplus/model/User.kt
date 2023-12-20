package com.hhplus.model

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table
class User(uuid: UUID, balance : Long) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var uuid : UUID = uuid

    var balance : Long = balance
}
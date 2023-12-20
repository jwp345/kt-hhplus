package com.hhplus.model.user

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table
class User(uuid: UUID, name: String, balance : Long) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var uuid : UUID = uuid

    var name : String = name

    var balance : Long = balance
}
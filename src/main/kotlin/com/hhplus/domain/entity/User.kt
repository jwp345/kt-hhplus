package com.hhplus.domain.entity

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.*

@Entity
@Table(name = "user")
class User(name: String, balance : Long) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "uuid", unique = true) @Tsid
    var uuid : Long ?= null

    var name : String = name

    var balance : Long = balance
}
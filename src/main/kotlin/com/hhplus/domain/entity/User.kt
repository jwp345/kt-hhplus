package com.hhplus.domain.entity

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.*

@Entity
@Table(name = "user")
class User(name: String, balance : Long) : BaseEntity() {
    @Column(name = "uuid", unique = true)
    @Id @Tsid
    var uuid : Long ?= null

    @Column(name = "name", length = 16)
    var name : String = name

    var balance : Long = balance
}
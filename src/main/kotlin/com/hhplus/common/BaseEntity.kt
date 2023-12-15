package com.hhplus.common

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    protected val createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(name = "MODIFIED_AT", nullable = false)
    protected var modifiedAt: LocalDateTime = LocalDateTime.now()
}
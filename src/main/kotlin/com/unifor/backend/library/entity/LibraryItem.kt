package com.unifor.backend.library.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "library_items")
class LibraryItem(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "target_id", nullable = false)
    val targetId: String,

    @Column(name = "target_type", nullable = false)
    val targetType: String, // "ASSET" or "GAME"

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

package com.unifor.backend.like.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "likes")
class Like(
    @Id
    val id: String = java.util.UUID.randomUUID().toString(),

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "target_type", nullable = false)
    val targetType: String, // GAME, ASSET

    @Column(name = "target_id", nullable = false)
    val targetId: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)




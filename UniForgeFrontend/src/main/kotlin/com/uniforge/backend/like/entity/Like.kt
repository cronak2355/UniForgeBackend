package com.uniforge.backend.like.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "likes")
class Like(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "target_type", nullable = false)
    val targetType: String, // GAME, ASSET

    @Column(name = "target_id", nullable = false)
    val targetId: Long,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

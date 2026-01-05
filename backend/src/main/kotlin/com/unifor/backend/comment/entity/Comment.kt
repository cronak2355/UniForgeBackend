package com.unifor.backend.comment.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "comments")
class Comment(
    @Id
    val id: String = java.util.UUID.randomUUID().toString(),

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "target_type", nullable = false)
    val targetType: String, // GAME, ASSET

    @Column(name = "target_id", nullable = false)
    val targetId: String,

    @Column(name = "content", length = 1000)
    val content: String,

    @Column(name = "parent_id")
    val parentId: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)




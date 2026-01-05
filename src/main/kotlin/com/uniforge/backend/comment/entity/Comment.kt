package com.uniforge.backend.comment.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "comments")
class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "target_type", nullable = false)
    val targetType: String, // GAME, ASSET

    @Column(name = "target_id", nullable = false)
    val targetId: Long,

    @Column(name = "content", length = 1000)
    val content: String,

    @Column(name = "parent_id")
    val parentId: Long? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

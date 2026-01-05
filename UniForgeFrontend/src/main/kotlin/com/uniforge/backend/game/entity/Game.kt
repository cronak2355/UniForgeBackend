package com.uniforge.backend.game.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "games")
class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "author_id", nullable = false)
    val authorId: Long,

    @Column(nullable = false)
    val title: String,

    val description: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

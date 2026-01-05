package com.unifor.backend.game.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "games")
class Game(
    @Id
    val id: String = java.util.UUID.randomUUID().toString(),

    @Column(name = "author_id", nullable = false)
    val authorId: String,

    @Column(nullable = false)
    val title: String,

    val description: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)




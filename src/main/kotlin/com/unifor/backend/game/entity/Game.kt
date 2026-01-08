package com.unifor.backend.game.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "games")
class Game(

    @Id
    @org.hibernate.annotations.UuidGenerator
    val id: String? = null,

    @Column(name = "author_id", nullable = false)
    val authorId: String,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "thumbnail_url")
    var thumbnailUrl: String? = null,

    @Column(name = "is_public", nullable = false)
    var isPublic: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
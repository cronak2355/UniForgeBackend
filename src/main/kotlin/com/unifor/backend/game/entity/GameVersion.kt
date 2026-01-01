package com.uniforge.backend.game.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "game_versions")
class GameVersion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    val game: Game,

    @Column(name = "s3_root_path", nullable = false)
    val s3RootPath: String,

    @Column(nullable = false)
    val status: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

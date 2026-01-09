package com.unifor.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "game_versions")
data class GameVersion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    val game: Game,

    @Column(nullable = false)
    val versionNumber: Int = 1, // Simple incremental versioning

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    val sceneJson: String, // Store the entire scene JSON blob

    @Column(nullable = true)
    val s3RootPath: String? = null,

    @Column(nullable = false)
    val status: String = "DRAFT",

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
)

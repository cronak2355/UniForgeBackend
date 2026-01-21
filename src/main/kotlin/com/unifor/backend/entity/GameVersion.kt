package com.unifor.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "game_versions")
data class GameVersion(
    @Id
    @Column(length = 36)
    val id: String = java.util.UUID.randomUUID().toString(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    val game: Game,

    @Column(nullable = false)
    val versionNumber: Int = 1, // Simple incremental versioning

    @Column(columnDefinition = "TEXT", nullable = false)
    val sceneJson: String, // Store the entire scene JSON blob

    @Column(nullable = true)
    val s3RootPath: String? = null,

    @Column(nullable = true)
    val status: String? = "DRAFT",

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun toString(): String {
        return "GameVersion(id='$id', versionNumber=$versionNumber, gameId='${game.id}')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameVersion

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

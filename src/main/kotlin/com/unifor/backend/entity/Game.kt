package com.unifor.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "games")
data class Game(
    @Id
    @Column(length = 36)
    val id: String = java.util.UUID.randomUUID().toString(),

    @Column(nullable = false)
    var title: String,

    @Column(nullable = true, length = 1000)
    var description: String? = null,

    @Column(nullable = true, length = 1024)
    var thumbnailUrl: String? = null,

    @Column(name = "is_public", nullable = true)
    var isPublic: Boolean? = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: User,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = true)
    var updatedAt: Instant? = Instant.now(),
    
    @OneToMany(mappedBy = "game", cascade = [CascadeType.ALL], orphanRemoval = true)
    var versions: MutableList<GameVersion> = mutableListOf()
) {
    override fun toString(): String {
        return "Game(id='$id', title='$title')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

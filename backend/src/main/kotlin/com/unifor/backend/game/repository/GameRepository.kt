package com.unifor.backend.game.repository

import com.unifor.backend.game.entity.Game
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GameRepository : JpaRepository<Game, String> {

    fun findByAuthorId(authorId: String): List<Game>

    fun findByIsPublicTrue(): List<Game>

    @Query("""
        SELECT DISTINCT g
        FROM Game g
        JOIN GameVersion v ON v.game = g
        WHERE v.status = 'PUBLISHED'
        ORDER BY g.createdAt DESC
    """)
    fun findPublishedGames(): List<Game>
}

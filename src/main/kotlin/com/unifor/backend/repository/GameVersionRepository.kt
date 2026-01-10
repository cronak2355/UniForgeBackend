package com.unifor.backend.repository

import com.unifor.backend.entity.GameVersion
import org.springframework.data.jpa.repository.JpaRepository

interface GameVersionRepository : JpaRepository<GameVersion, Long> {
    fun findByGame_IdOrderByVersionNumberDesc(gameId: String): List<GameVersion>
    fun findTopByGameAndStatusOrderByCreatedAtDesc(game: com.unifor.backend.entity.Game, status: String): GameVersion?
}

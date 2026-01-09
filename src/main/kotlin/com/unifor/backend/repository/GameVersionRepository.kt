package com.unifor.backend.repository

import com.unifor.backend.entity.GameVersion
import org.springframework.data.jpa.repository.JpaRepository

interface GameVersionRepository : JpaRepository<GameVersion, Long> {
    fun findByGameIdOrderByVersionNumberDesc(gameId: String): List<GameVersion>
    fun findTopByGameAndStatusOrderByCreatedAtDesc(game: com.unifor.backend.entity.Game, status: String): GameVersion?
}

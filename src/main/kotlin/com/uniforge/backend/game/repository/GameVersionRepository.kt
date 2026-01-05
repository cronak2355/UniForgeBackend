package com.uniforge.backend.game.repository

import com.uniforge.backend.game.entity.GameVersion
import org.springframework.data.jpa.repository.JpaRepository

interface GameVersionRepository : JpaRepository<GameVersion, Long> {
    fun findTopByGameIdAndStatusOrderByCreatedAtDesc(
        gameId: Long,
        status: String
    ): GameVersion?
}

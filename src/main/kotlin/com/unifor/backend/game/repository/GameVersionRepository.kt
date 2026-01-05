package com.unifor.backend.game.repository

import com.unifor.backend.game.entity.GameVersion
import org.springframework.data.jpa.repository.JpaRepository

interface GameVersionRepository : JpaRepository<GameVersion, String> {
    fun findTopByGameIdAndStatusOrderByCreatedAtDesc(
        gameId: String,
        status: String
    ): GameVersion?
}




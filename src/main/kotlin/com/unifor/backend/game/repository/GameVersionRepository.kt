package com.unifor.backend.game.repository

import com.unifor.backend.game.entity.Game
import com.unifor.backend.game.entity.GameVersion
import org.springframework.data.jpa.repository.JpaRepository

interface GameVersionRepository : JpaRepository<GameVersion, String> {

    fun findTopByGameAndStatusOrderByCreatedAtDesc(
        game: Game,
        status: String
    ): GameVersion?
}

package com.uniforge.backend.game.repository

import com.uniforge.backend.game.entity.Game
import com.uniforge.backend.game.entity.GameVersion
import org.springframework.data.jpa.repository.JpaRepository

interface GameVersionRepository : JpaRepository<GameVersion, Long> {

    fun findTopByGameAndStatusOrderByCreatedAtDesc(
        game: Game,
        status: String
    ): GameVersion?
}

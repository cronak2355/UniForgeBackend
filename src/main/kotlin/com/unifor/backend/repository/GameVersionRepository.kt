package com.unifor.backend.repository

import com.unifor.backend.entity.GameVersion
import org.springframework.data.jpa.repository.JpaRepository

interface GameVersionRepository : JpaRepository<GameVersion, Long> {
    fun findByGameIdOrderByVersionNumberDesc(gameId: Long): List<GameVersion>
}

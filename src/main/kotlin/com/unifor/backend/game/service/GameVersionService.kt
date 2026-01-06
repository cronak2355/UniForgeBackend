package com.uniforge.backend.game.service

import com.uniforge.backend.game.entity.GameVersion
import com.uniforge.backend.game.repository.GameRepository
import com.uniforge.backend.game.repository.GameVersionRepository
import org.springframework.stereotype.Service

@Service
class GameVersionService(
    private val gameRepository: GameRepository,
    private val versionRepository: GameVersionRepository
) {
    fun createVersion(gameId: Long, s3Path: String): GameVersion {
        val game = gameRepository.findById(gameId).orElseThrow()
        return versionRepository.save(
            GameVersion(
                game = game,
                s3RootPath = s3Path,
                status = "DRAFT"
            )
        )
    }

    fun publish(versionId: Long) {
        val version = versionRepository.findById(versionId).orElseThrow()
        version.status = "PUBLISHED"
    }
}

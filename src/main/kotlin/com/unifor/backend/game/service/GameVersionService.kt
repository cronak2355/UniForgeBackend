package com.unifor.backend.game.service

import com.unifor.backend.game.entity.GameVersion
import com.unifor.backend.game.repository.GameRepository
import com.unifor.backend.game.repository.GameVersionRepository
import org.springframework.stereotype.Service

@Service
class GameVersionService(
    private val gameRepository: GameRepository,
    private val versionRepository: GameVersionRepository
) {
    fun createVersion(gameId: String, s3Path: String): GameVersion {
        val game = gameRepository.findById(gameId).orElseThrow()
        return versionRepository.save(
            GameVersion(
                game = game,
                s3RootPath = s3Path,
                status = "DRAFT"
            )
        )
    }

    fun publish(versionId: String) {
        val version = versionRepository.findById(versionId).orElseThrow()
        versionRepository.save(
            GameVersion(
                id = version.id,
                game = version.game,
                s3RootPath = version.s3RootPath,
                status = "PUBLISHED",
                createdAt = version.createdAt
            )
        )
    }
}




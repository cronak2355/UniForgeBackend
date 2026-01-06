package com.uniforge.backend.game.service

import com.uniforge.backend.game.dto.GameSummaryResponse
import com.uniforge.backend.game.repository.GameRepository
import com.uniforge.backend.game.repository.GameVersionRepository
import org.springframework.stereotype.Service

@Service
class GameQueryService(
    private val gameRepository: GameRepository,
    private val versionRepository: GameVersionRepository
) {

    fun getMyGames(authorId: Long): List<GameSummaryResponse> {
        return gameRepository.findByAuthorId(authorId).map { game ->
            val latestVersion = versionRepository
                .findTopByGameAndStatusOrderByCreatedAtDesc(game, "PUBLISHED")

            GameSummaryResponse(
                gameId = game.id,
                title = game.title,
                description = game.description,
                thumbnailUrl = game.thumbnailUrl,
                authorId = game.authorId,
                latestVersionId = latestVersion?.id,
                createdAt = game.createdAt
            )
        }
    }

    fun getPublicGames(): List<GameSummaryResponse> {
        return gameRepository.findByIsPublicTrue().map { game ->
            val latestVersion = versionRepository
                .findTopByGameAndStatusOrderByCreatedAtDesc(game, "PUBLISHED")

            GameSummaryResponse(
                gameId = game.id,
                title = game.title,
                description = game.description,
                thumbnailUrl = game.thumbnailUrl,
                authorId = game.authorId,
                latestVersionId = latestVersion?.id,
                createdAt = game.createdAt
            )
        }
    }
}

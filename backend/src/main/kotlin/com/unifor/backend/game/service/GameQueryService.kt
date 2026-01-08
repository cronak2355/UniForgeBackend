package com.unifor.backend.game.service

import com.unifor.backend.game.dto.GameSummaryResponse
import com.unifor.backend.game.entity.Game
import com.unifor.backend.game.repository.GameRepository
import com.unifor.backend.game.repository.GameVersionRepository
import org.springframework.stereotype.Service

@Service
class GameQueryService(
    private val gameRepository: GameRepository,
    private val versionRepository: GameVersionRepository
) {

    fun createGame(
        authorId: String,
        title: String,
        description: String?
    ): Game {
        return gameRepository.save(
            Game(
                authorId = authorId,
                title = title,
                description = description
            )
        )
    }

    fun getMyGames(authorId: String): List<GameSummaryResponse> {
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

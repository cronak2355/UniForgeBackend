package com.unifor.backend.dto

import com.unifor.backend.entity.Game

data class GameSummaryDTO(
    val gameId: String, // UUID
    val title: String,
    val description: String?,
    val thumbnailUrl: String?,
    val authorId: String,
    val latestVersionId: String?,
    val isPublic: Boolean,
    val createdAt: String
) {
    companion object {
        fun from(game: Game): GameSummaryDTO = GameSummaryDTO(
            gameId = game.id,
            title = game.title,
            description = game.description,
            thumbnailUrl = game.thumbnailUrl,
            authorId = game.author.id,
            latestVersionId = game.versions.maxByOrNull { it.versionNumber }?.id,
            isPublic = game.isPublic,
            createdAt = game.createdAt.toString()
        )
    }
}

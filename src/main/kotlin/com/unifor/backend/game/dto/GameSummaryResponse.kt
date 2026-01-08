package com.unifor.backend.game.dto

import java.time.LocalDateTime

data class GameSummaryResponse(
    val gameId: String?,
    val title: String,
    val description: String?,
    val thumbnailUrl: String?,
    val authorId: String,
    val latestVersionId: String?,
    val createdAt: LocalDateTime
)
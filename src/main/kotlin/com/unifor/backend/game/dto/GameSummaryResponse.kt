package com.unifor.backend.game.dto

import java.time.LocalDateTime

data class GameSummaryResponse(
    val gameId: Long,
    val title: String,
    val description: String?,
    val thumbnailUrl: String?,
    val authorId: Long,
    val latestVersionId: String?,
    val createdAt: LocalDateTime
)
package com.uniforge.backend.game.dto

import java.time.LocalDateTime

data class GameSummaryResponse(
    val gameId: Long,
    val title: String,
    val description: String?,
    val thumbnailUrl: String?,
    val authorId: Long,
    val latestVersionId: Long?,
    val createdAt: LocalDateTime
)
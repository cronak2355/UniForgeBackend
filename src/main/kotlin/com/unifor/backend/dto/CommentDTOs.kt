package com.unifor.backend.dto

import java.time.Instant

data class CommentRequest(
    val content: String
)

data class CommentResponse(
    val id: String,
    val content: String,
    val authorId: String,
    val authorName: String,
    val authorProfileImage: String?,
    val createdAt: Instant
)

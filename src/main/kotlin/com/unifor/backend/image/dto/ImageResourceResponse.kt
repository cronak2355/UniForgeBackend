package com.uniforge.backend.image.dto

import java.time.LocalDateTime

data class ImageResourceResponse(
    val id: Long,
    val ownerType: String,
    val ownerId: Long,
    val imageType: String,
    val s3Key: String,
    val createdAt: LocalDateTime
)

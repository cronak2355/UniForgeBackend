package com.unifor.backend.image.dto

import java.time.LocalDateTime

data class ImageResourceResponse(
    val id: String,
    val ownerType: String,
    val ownerId: String,
    val imageType: String,
    val s3Key: String,
    val createdAt: LocalDateTime
)




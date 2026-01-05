package com.uniforge.backend.image.dto

data class CreateImageResourceRequest(
    val ownerType: String,
    val ownerId: Long,
    val imageType: String,
    val s3Key: String,
    val contentType: String?
)

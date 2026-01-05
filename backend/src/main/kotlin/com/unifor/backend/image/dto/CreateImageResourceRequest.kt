package com.unifor.backend.image.dto

data class CreateImageResourceRequest(
    val ownerType: String,
    val ownerId: String,
    val imageType: String,
    val s3Key: String,
    val contentType: String?
)




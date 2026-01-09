package com.unifor.backend.dto

data class GameVersionResponseDTO(
    val versionId: Long,
    val versionNumber: Int,
    val sceneJson: String,
    val createdAt: String
)

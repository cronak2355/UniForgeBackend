package com.unifor.backend.dto

data class GameVersionResponseDTO(
    val versionId: String,
    val versionNumber: Int,
    val sceneJson: String,
    val createdAt: String
)

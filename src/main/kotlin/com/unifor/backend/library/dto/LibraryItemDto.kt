package com.unifor.backend.library.dto

import java.time.Instant
import java.time.LocalDateTime

data class LibraryItemResponse(
    val id: String,
    val userId: String,
    val itemType: String,
    val refId: String,
    val collectionId: String?,
    val createdAt: LocalDateTime,
    val asset: LibraryAssetSummary?
)

data class LibraryAssetSummary(
    val id: String,
    val name: String,
    val authorId: String,
    val authorName: String?,
    val imageUrl: String?,
    val createdAt: Instant,
    val genre: String?,
    val assetType: String?
)

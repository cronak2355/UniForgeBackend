package com.unifor.backend.library.service

import com.unifor.backend.library.entity.LibraryItem
import com.unifor.backend.library.repository.LibraryItemRepository
import com.unifor.backend.repository.AssetRepository
import com.unifor.backend.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class LibraryItemWithAsset(
    val item: LibraryItem,
    val assetName: String?,
    val assetImageUrl: String?,
    val assetType: String?,
    val authorId: String?,
    val authorName: String?
)

@Service
class LibraryService(
    private val libraryItemRepository: LibraryItemRepository,
    private val assetRepository: AssetRepository,
    private val userRepository: UserRepository
) {
    
    @Transactional
    fun addToLibrary(userId: String, targetId: String, targetType: String) {
        if (libraryItemRepository.existsByUserIdAndTargetIdAndTargetType(userId, targetId, targetType)) {
            return // Already exists
        }

        val item = LibraryItem(
            userId = userId,
            targetId = targetId,
            targetType = targetType
        )
        libraryItemRepository.save(item)
    }

    fun getLibrary(userId: String): List<LibraryItem> {
        return libraryItemRepository.findByUserId(userId)
    }

    /**
     * Get library items with asset details and author names (optimized - batch queries)
     */
    fun getLibraryWithDetails(userId: String): List<LibraryItemWithAsset> {
        val items = libraryItemRepository.findByUserId(userId)
        
        // Get all asset IDs
        val assetIds = items
            .filter { it.targetType == "ASSET" && it.targetId != null }
            .mapNotNull { it.targetId }
        
        // Batch fetch assets
        val assetsById = if (assetIds.isNotEmpty()) {
            assetRepository.findAllById(assetIds).associateBy { it.id }
        } else {
            emptyMap()
        }
        
        // Batch fetch author users
        val authorIds = assetsById.values.map { it.authorId }.distinct()
        val usersById = if (authorIds.isNotEmpty()) {
            userRepository.findAllById(authorIds).associateBy { it.id }
        } else {
            emptyMap()
        }
        
        return items.map { item ->
            val asset = item.targetId?.let { assetsById[it] }
            val author = asset?.let { usersById[it.authorId] }
            LibraryItemWithAsset(
                item = item,
                assetName = asset?.name,
                assetImageUrl = asset?.imageUrl,
                assetType = asset?.assetType,
                authorId = asset?.authorId,
                authorName = author?.name
            )
        }
    }
}

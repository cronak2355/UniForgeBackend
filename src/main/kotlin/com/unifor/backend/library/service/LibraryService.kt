package com.unifor.backend.library.service

import com.unifor.backend.library.dto.LibraryAssetSummary
import com.unifor.backend.library.dto.LibraryItemResponse
import com.unifor.backend.library.entity.LibraryCollection
import com.unifor.backend.library.entity.LibraryItem
import com.unifor.backend.library.repository.LibraryCollectionRepository
import com.unifor.backend.library.repository.LibraryRepository
import com.unifor.backend.repository.AssetRepository
import com.unifor.backend.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class LibraryService(
    private val libraryRepository: LibraryRepository,
    private val libraryCollectionRepository: LibraryCollectionRepository,
    private val assetRepository: AssetRepository,
    private val userRepository: UserRepository
) {
    fun getUserLibrary(userId: String): List<LibraryItemResponse> {
        val items = libraryRepository.findAllByUserId(userId)
        val assetIds = items.filter { it.itemType == "ASSET" }.map(LibraryItem::refId)
        val assetsById = if (assetIds.isNotEmpty()) {
            assetRepository.findAllById(assetIds).associateBy { it.id }
        } else {
            emptyMap()
        }

        // Batch fetch all author users
        val authorIds = assetsById.values.map { it.authorId }.distinct()
        val usersById = if (authorIds.isNotEmpty()) {
            userRepository.findAllById(authorIds).associateBy { it.id }
        } else {
            emptyMap()
        }

        return items.map { item ->
            val asset = assetsById[item.refId]
            val authorName = asset?.let { usersById[it.authorId]?.name }
            LibraryItemResponse(
                id = item.id,
                userId = item.userId,
                itemType = item.itemType,
                refId = item.refId,
                collectionId = item.collectionId,
                createdAt = item.createdAt,
                asset = asset?.let {
                    LibraryAssetSummary(
                        id = it.id,
                        name = it.name,
                        authorId = it.authorId,
                        authorName = authorName,
                        imageUrl = it.imageUrl,
                        createdAt = it.createdAt,
                        genre = it.genre,
                        assetType = it.assetType
                    )
                }
            )
        }
    }

    fun addToLibrary(userId: String, refId: String, itemType: String): LibraryItem {
        if (libraryRepository.existsByUserIdAndRefId(userId, refId)) {
             val existing = libraryRepository.findByUserIdAndRefId(userId, refId)
             // safe unwrap or throw
             if (existing != null) return existing
             throw IllegalStateException("Item exists check failed")
        }

        return libraryRepository.save(
            LibraryItem(
                userId = userId,
                refId = refId,
                itemType = itemType
            )
        )
    }

    // Collection Methods
    fun createCollection(userId: String, name: String): LibraryCollection {
        return libraryCollectionRepository.save(
            LibraryCollection(
                userId = userId,
                name = name
            )
        )
    }

    fun getUserCollections(userId: String): List<LibraryCollection> =
        libraryCollectionRepository.findAllByUserId(userId)

    fun moveItemToCollection(userId: String, itemId: String, collectionId: String?): LibraryItem {
        val item = libraryRepository.findById(itemId).orElseThrow { IllegalArgumentException("Item not found") }
        if (item.userId != userId) throw IllegalArgumentException("Not your item")

        // Verify collection ownership if collectionId is present
        if (collectionId != null) {
            val collection = libraryCollectionRepository.findById(collectionId).orElseThrow { IllegalArgumentException("Collection not found") }
            if (collection.userId != userId) throw IllegalArgumentException("Not your collection")
        }

        item.collectionId = collectionId
        return libraryRepository.save(item)
    }
}

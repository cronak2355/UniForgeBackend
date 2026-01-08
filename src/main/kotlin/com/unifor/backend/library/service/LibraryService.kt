package com.unifor.backend.library.service

import com.unifor.backend.library.entity.LibraryCollection
import com.unifor.backend.library.entity.LibraryItem
import com.unifor.backend.library.repository.LibraryCollectionRepository
import com.unifor.backend.library.repository.LibraryRepository
import org.springframework.stereotype.Service

@Service
class LibraryService(
    private val libraryRepository: LibraryRepository,
    private val libraryCollectionRepository: LibraryCollectionRepository
) {
    fun getUserLibrary(userId: String): List<LibraryItem> =
        libraryRepository.findAllByUserId(userId)

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

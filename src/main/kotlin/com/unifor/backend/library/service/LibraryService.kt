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
import org.springframework.transaction.annotation.Transactional

@Service
class LibraryService(
    private val libraryItemRepository: LibraryItemRepository
    private val libraryRepository: LibraryRepository,
    private val libraryCollectionRepository: LibraryCollectionRepository,
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
}

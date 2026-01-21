package com.unifor.backend.library.service

import com.unifor.backend.library.entity.LibraryItem
import com.unifor.backend.library.repository.LibraryItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LibraryService(
    private val libraryItemRepository: LibraryItemRepository
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

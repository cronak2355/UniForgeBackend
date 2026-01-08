package com.unifor.backend.library.service

import com.unifor.backend.library.repository.LibraryRepository
import org.springframework.stereotype.Service

@Service
class LibraryService(
    private val libraryRepository: LibraryRepository
) {
    fun getUserLibrary(userId: String) =
        libraryRepository.findAllByUserId(userId)

    fun addToLibrary(userId: String, refId: String, itemType: String): com.unifor.backend.library.entity.LibraryItem {
        if (libraryRepository.existsByUserIdAndRefId(userId, refId)) {
            throw IllegalArgumentException("Already in library")
        }
        
        return libraryRepository.save(
            com.unifor.backend.library.entity.LibraryItem(
                userId = userId,
                refId = refId,
                itemType = itemType
            )
        )
    }
}


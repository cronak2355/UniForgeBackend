package com.unifor.backend.library.service

import org.springframework.stereotype.Service

@Service
class LibraryService {
    
    // Placeholder implementation
    fun addToLibrary(userId: String, targetId: String, targetType: String) {
        // Build logic to add asset/game to user's library
        println("Adding $targetType $targetId to user $userId library")
    }
}

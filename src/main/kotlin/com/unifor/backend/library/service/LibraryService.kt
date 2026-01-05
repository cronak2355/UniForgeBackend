package com.unifor.backend.library.service

import com.unifor.backend.library.repository.LibraryRepository
import org.springframework.stereotype.Service

@Service
class LibraryService(
    private val libraryRepository: LibraryRepository
) {
    fun getUserLibrary(userId: String) =
        libraryRepository.findAllByUserId(userId)
}


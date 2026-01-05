package com.uniforge.backend.library.service

import com.uniforge.backend.library.repository.LibraryRepository
import org.springframework.stereotype.Service

@Service
class LibraryService(
    private val libraryRepository: LibraryRepository
) {
    fun getUserLibrary(userId: Long) =
        libraryRepository.findAllByUserId(userId)
}

package com.unifor.backend.library.repository

import com.unifor.backend.library.entity.LibraryCollection
import org.springframework.data.jpa.repository.JpaRepository

interface LibraryCollectionRepository : JpaRepository<LibraryCollection, String> {
    fun findAllByUserId(userId: String): List<LibraryCollection>
}

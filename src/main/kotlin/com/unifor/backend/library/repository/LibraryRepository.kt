package com.unifor.backend.library.repository

import com.unifor.backend.library.entity.LibraryItem
import org.springframework.data.jpa.repository.JpaRepository

interface LibraryRepository : JpaRepository<LibraryItem, String> {
    fun findAllByUserId(userId: String): List<LibraryItem>
    fun existsByUserIdAndRefId(userId: String, refId: String): Boolean
    fun findByUserIdAndRefId(userId: String, refId: String): LibraryItem?
}


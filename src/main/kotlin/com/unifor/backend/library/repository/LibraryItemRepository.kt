package com.unifor.backend.library.repository

import com.unifor.backend.library.entity.LibraryItem
import org.springframework.data.jpa.repository.JpaRepository

interface LibraryItemRepository : JpaRepository<LibraryItem, String> {
    fun existsByUserIdAndTargetIdAndTargetType(userId: String, targetId: String, targetType: String): Boolean
    fun findByUserId(userId: String): List<LibraryItem>
}

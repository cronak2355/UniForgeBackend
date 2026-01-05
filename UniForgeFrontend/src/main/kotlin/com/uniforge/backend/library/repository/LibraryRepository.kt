package com.uniforge.backend.library.repository

import com.uniforge.backend.library.entity.LibraryItem
import org.springframework.data.jpa.repository.JpaRepository

interface LibraryRepository : JpaRepository<LibraryItem, Long> {
    fun findAllByUserId(userId: Long): List<LibraryItem>
}

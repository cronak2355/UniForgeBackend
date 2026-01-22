package com.unifor.backend.repository

import com.unifor.backend.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, String> {
    fun findByGameIdOrderByCreatedAtDesc(gameId: String): List<Comment>
}

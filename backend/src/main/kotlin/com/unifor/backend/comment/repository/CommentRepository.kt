package com.unifor.backend.comment.repository

import com.unifor.backend.comment.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByTargetTypeAndTargetId(type: String, targetId: Long): List<Comment>
}

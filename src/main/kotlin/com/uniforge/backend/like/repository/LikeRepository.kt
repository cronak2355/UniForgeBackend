package com.uniforge.backend.like.repository

import com.uniforge.backend.like.entity.Like
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<Like, Long> {
    fun countByTargetTypeAndTargetId(targetType: String, targetId: Long): Long
}

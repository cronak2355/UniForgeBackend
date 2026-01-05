package com.unifor.backend.like.repository

import com.unifor.backend.like.entity.Like
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<Like, Long> {
    fun countByTargetTypeAndTargetId(targetType: String, targetId: Long): Long
}

package com.unifor.backend.like.repository

import com.unifor.backend.like.entity.Like
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<Like, String> {
    fun countByTargetTypeAndTargetId(targetType: String, targetId: String): Long
}




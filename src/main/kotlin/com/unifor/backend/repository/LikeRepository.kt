package com.unifor.backend.repository

import com.unifor.backend.entity.Like
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<Like, String> {
    fun countByTargetIdAndTargetType(targetId: String, targetType: String): Long
    fun existsByUserIdAndTargetIdAndTargetType(userId: String, targetId: String, targetType: String): Boolean
    fun findByUserIdAndTargetIdAndTargetType(userId: String, targetId: String, targetType: String): Like?
}

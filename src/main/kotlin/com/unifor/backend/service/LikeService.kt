package com.unifor.backend.service

import com.unifor.backend.entity.Like
import com.unifor.backend.repository.LikeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeService(
    private val likeRepository: LikeRepository
) {
    @Transactional(readOnly = true)
    fun getLikeCount(targetId: String, targetType: String): Long {
        return likeRepository.countByTargetIdAndTargetType(targetId, targetType)
    }

    @Transactional(readOnly = true)
    fun hasLiked(userId: String, targetId: String, targetType: String): Boolean {
        return likeRepository.existsByUserIdAndTargetIdAndTargetType(userId, targetId, targetType)
    }

    @Transactional
    fun toggleLike(userId: String, targetId: String, targetType: String): Boolean {
        val existingLike = likeRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType)
        return if (existingLike != null) {
            likeRepository.delete(existingLike)
            false // unliked
        } else {
            likeRepository.save(Like(userId = userId, targetId = targetId, targetType = targetType))
            true // liked
        }
    }
}

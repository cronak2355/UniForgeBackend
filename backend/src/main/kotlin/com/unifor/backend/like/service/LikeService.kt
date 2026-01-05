package com.unifor.backend.like.service

import com.unifor.backend.like.entity.Like
import com.unifor.backend.like.repository.LikeRepository
import org.springframework.stereotype.Service

@Service
class LikeService(
    private val likeRepository: LikeRepository
) {
    fun like(userId: Long, type: String, targetId: Long) {
        likeRepository.save(
            Like(
                userId = userId,
                targetType = type,
                targetId = targetId
            )
        )
    }

    fun count(type: String, targetId: Long) =
        likeRepository.countByTargetTypeAndTargetId(type, targetId)
}

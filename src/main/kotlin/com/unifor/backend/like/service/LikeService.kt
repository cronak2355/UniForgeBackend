package com.unifor.backend.like.service

import com.unifor.backend.like.entity.Like
import com.unifor.backend.like.repository.LikeRepository
import org.springframework.stereotype.Service

@Service
class LikeService(
    private val likeRepository: LikeRepository
) {
    fun like(userId: String, type: String, targetId: String) {
        likeRepository.save(
            Like(
                userId = userId,
                targetType = type,
                targetId = targetId
            )
        )
    }

    fun count(type: String, targetId: String) =
        likeRepository.countByTargetTypeAndTargetId(type, targetId)
}




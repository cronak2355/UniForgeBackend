package com.uniforge.backend.comment.service

import com.uniforge.backend.comment.entity.Comment
import com.uniforge.backend.comment.repository.CommentRepository
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository
) {
    fun write(
        userId: Long,
        type: String,
        targetId: Long,
        content: String,
        parentId: Long?
    ) {
        commentRepository.save(
            Comment(
                userId = userId,
                targetType = type,
                targetId = targetId,
                content = content,
                parentId = parentId
            )
        )
    }

    fun list(type: String, targetId: Long) =
        commentRepository.findAllByTargetTypeAndTargetId(type, targetId)
}

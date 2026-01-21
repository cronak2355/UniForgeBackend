package com.unifor.backend.service

import com.unifor.backend.dto.CommentRequest
import com.unifor.backend.dto.CommentResponse
import com.unifor.backend.entity.Comment
import com.unifor.backend.repository.CommentRepository
import com.unifor.backend.repository.GameRepository
import com.unifor.backend.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getCommentsByGame(gameId: String): List<CommentResponse> {
        return commentRepository.findByGameIdOrderByCreatedAtDesc(gameId)
            .map { toResponse(it) }
    }

    @Transactional
    fun createComment(gameId: String, authorId: String, request: CommentRequest): CommentResponse {
        val game = gameRepository.findById(gameId)
            .orElseThrow { EntityNotFoundException("Game not found with id $gameId") }
        
        val user = userRepository.findById(authorId)
            .orElseThrow { EntityNotFoundException("User not found with id $authorId") }

        val comment = Comment(
            content = request.content,
            game = game,
            author = user
        )
        
        val savedComment = commentRepository.save(comment)
        return toResponse(savedComment)
    }

    @Transactional
    fun deleteComment(commentId: String, userId: String) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { EntityNotFoundException("Comment not found with id $commentId") }

        if (comment.author.id != userId) {
            throw IllegalAccessException("You are not authorized to delete this comment")
        }

        commentRepository.delete(comment)
    }

    private fun toResponse(comment: Comment): CommentResponse {
        return CommentResponse(
            id = comment.id,
            content = comment.content,
            authorId = comment.author.id,
            authorName = comment.author.name,
            authorProfileImage = comment.author.profileImage,
            createdAt = comment.createdAt
        )
    }
}

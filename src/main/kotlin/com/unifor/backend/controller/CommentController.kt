package com.unifor.backend.controller

import com.unifor.backend.dto.CommentRequest
import com.unifor.backend.dto.CommentResponse
import com.unifor.backend.service.CommentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/games/{gameId}/comments")
class CommentController(
    private val commentService: CommentService
) {

    @GetMapping
    fun getComments(@PathVariable gameId: String): ResponseEntity<List<CommentResponse>> {
        val comments = commentService.getCommentsByGame(gameId)
        return ResponseEntity.ok(comments)
    }

    @PostMapping
    fun createComment(
        @PathVariable gameId: String,
        @RequestParam authorId: String,
        @RequestBody request: CommentRequest
    ): ResponseEntity<CommentResponse> {
        val comment = commentService.createComment(gameId, authorId, request)
        return ResponseEntity.ok(comment)
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable gameId: String,
        @PathVariable commentId: String,
        @RequestParam userId: String
    ): ResponseEntity<Void> {
        return try {
            commentService.deleteComment(commentId, userId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalAccessException) {
            ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build()
        } catch (e: jakarta.persistence.EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }
}

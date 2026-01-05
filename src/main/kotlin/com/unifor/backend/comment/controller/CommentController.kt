package com.unifor.backend.comment.controller

import com.unifor.backend.comment.service.CommentService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping
    fun write(
        @RequestParam userId: String,
        @RequestParam type: String,
        @RequestParam targetId: String,
        @RequestParam content: String,
        @RequestParam(required = false) parentId: String?
    ) = commentService.write(userId, type, targetId, content, parentId)

    @GetMapping
    fun list(
        @RequestParam type: String,
        @RequestParam targetId: String
    ) = commentService.list(type, targetId)
}




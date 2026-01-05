package com.uniforge.backend.comment.controller

import com.uniforge.backend.comment.service.CommentService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping
    fun write(
        @RequestParam userId: Long,
        @RequestParam type: String,
        @RequestParam targetId: Long,
        @RequestParam content: String,
        @RequestParam(required = false) parentId: Long?
    ) = commentService.write(userId, type, targetId, content, parentId)

    @GetMapping
    fun list(
        @RequestParam type: String,
        @RequestParam targetId: Long
    ) = commentService.list(type, targetId)
}

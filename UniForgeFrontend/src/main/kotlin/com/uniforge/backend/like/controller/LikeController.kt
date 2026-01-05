package com.uniforge.backend.like.controller

import com.uniforge.backend.like.service.LikeService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/likes")
class LikeController(
    private val likeService: LikeService
) {

    @PostMapping
    fun like(
        @RequestParam userId: Long,
        @RequestParam type: String,
        @RequestParam targetId: Long
    ) = likeService.like(userId, type, targetId)

    @GetMapping("/count")
    fun count(
        @RequestParam type: String,
        @RequestParam targetId: Long
    ) = likeService.count(type, targetId)
}

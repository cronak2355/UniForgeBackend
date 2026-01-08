package com.unifor.backend.like.controller

import com.unifor.backend.like.service.LikeService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/likes")
class LikeController(
    private val likeService: LikeService
) {

    @PostMapping
    fun like(
        @RequestParam userId: String,
        @RequestParam type: String,
        @RequestParam targetId: String
    ) = likeService.like(userId, type, targetId)

    @GetMapping("/count")
    fun count(
        @RequestParam type: String,
        @RequestParam targetId: String
    ) = likeService.count(type, targetId)
}




package com.unifor.backend.controller

import com.unifor.backend.service.LikeService
import com.unifor.backend.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/likes")
class LikeController(
    private val likeService: LikeService
) {

    @GetMapping("/count")
    fun getLikeCount(
        @RequestParam type: String,
        @RequestParam targetId: String
    ): ResponseEntity<Long> {
        val count = likeService.getLikeCount(targetId, type)
        return ResponseEntity.ok(count)
    }

    @GetMapping
    fun hasLiked(
        @RequestParam userId: String,
        @RequestParam type: String,
        @RequestParam targetId: String
    ): ResponseEntity<Boolean> {
        val liked = likeService.hasLiked(userId, targetId, type)
        return ResponseEntity.ok(liked)
    }

    @PostMapping
    fun toggleLike(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: ToggleLikeRequest
    ): ResponseEntity<Boolean> {
        val liked = likeService.toggleLike(user.id, request.targetId, request.type)
        return ResponseEntity.ok(liked)
    }
}

data class ToggleLikeRequest(
    val targetId: String,
    val type: String
)

package com.unifor.backend.game.controller

import com.unifor.backend.common.s3.S3Uploader
import com.unifor.backend.game.service.GameQueryService
import com.unifor.backend.game.service.GameVersionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/games")
class GameController(
    private val gameQueryService: GameQueryService,
    private val versionService: GameVersionService,
    private val s3Uploader: S3Uploader
) {

    @PostMapping
    fun createGame(
        @RequestParam authorId: String,
        @RequestParam title: String,
        @RequestParam(required = false) description: String?
    ) = gameQueryService.createGame(authorId, title, description)

    @PostMapping("/{gameId}/versions")
    fun createVersion(
        @PathVariable gameId: String,
        @RequestBody sceneJson: String
    ): Any {
        val s3Path = s3Uploader.uploadJson(
            key = "games/$gameId/${System.currentTimeMillis()}/scene.json",
            json = sceneJson
        )

        return versionService.createVersion(gameId, s3Path)
    }

    @PostMapping("/versions/{versionId}/publish")
    fun publish(@PathVariable versionId: String) {
        versionService.publish(versionId)
    }

}

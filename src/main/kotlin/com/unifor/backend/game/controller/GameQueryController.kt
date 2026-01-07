package com.unifor.backend.game.controller

import com.unifor.backend.game.service.GameQueryService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/games")
class GameQueryController(
    private val gameQueryService: GameQueryService
) {

    @GetMapping("/my")
    fun getMyGames(
        @RequestParam authorId: Long
    ) = gameQueryService.getMyGames(authorId)

    @GetMapping("/public")
    fun getPublicGames() =
        gameQueryService.getPublicGames()
}

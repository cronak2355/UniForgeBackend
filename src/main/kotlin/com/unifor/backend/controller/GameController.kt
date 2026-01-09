package com.unifor.backend.controller

import com.unifor.backend.dto.GameSummaryDTO
import com.unifor.backend.service.GameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/games")
class GameController(
    private val gameService: GameService
) {

    @PostMapping
    fun createGame(
        @RequestParam authorId: String,
        @RequestParam title: String,
        @RequestParam(required = false) description: String?
    ): ResponseEntity<GameSummaryDTO> {
        val game = gameService.createGame(authorId, title, description)
        return ResponseEntity.ok(game)
    }

    @GetMapping("/my")
    fun getMyGames(@RequestParam authorId: String): ResponseEntity<List<GameSummaryDTO>> {
        val games = gameService.getMyGames(authorId)
        return ResponseEntity.ok(games)
    }

    @GetMapping("/public")
    fun getPublicGames(): ResponseEntity<List<GameSummaryDTO>> {
        val games = gameService.getPublicGames()
        return ResponseEntity.ok(games)
    }

    @PostMapping("/{gameId}/versions")
    fun saveGameVersion(
        @PathVariable gameId: Long,
        @RequestBody sceneJson: String // Using String to accept raw JSON from frontend
    ): ResponseEntity<Void> {
        gameService.saveGameVersion(gameId, sceneJson)
        return ResponseEntity.ok().build()
    }
}

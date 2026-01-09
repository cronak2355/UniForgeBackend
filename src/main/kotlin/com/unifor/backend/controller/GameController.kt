package com.unifor.backend.controller

import com.unifor.backend.dto.GameSummaryDTO
import com.unifor.backend.service.GameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/games")
class GameController(
    private val gameService: GameService,
    private val objectMapper: com.fasterxml.jackson.databind.ObjectMapper
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
        @PathVariable gameId: String,
        @RequestBody payload: Map<String, Any>
    ): ResponseEntity<Void> {
        val sceneJson = objectMapper.writeValueAsString(payload)
        gameService.saveGameVersion(gameId, sceneJson)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{gameId}/versions/latest")
    fun getLatestVersion(
        @PathVariable gameId: String
    ): ResponseEntity<com.unifor.backend.dto.GameVersionResponseDTO> {
        return try {
            val version = gameService.getLatestGameVersion(gameId)
            ResponseEntity.ok(version)
        } catch (e: jakarta.persistence.EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }
}

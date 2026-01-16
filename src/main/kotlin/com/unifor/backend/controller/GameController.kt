package com.unifor.backend.controller

import com.unifor.backend.dto.GameSummaryDTO
import com.unifor.backend.service.GameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/games")
class GameController(
    private val gameService: GameService,
    private val objectMapper: com.fasterxml.jackson.databind.ObjectMapper,
    private val imageResourceRepository: com.unifor.backend.image.repository.ImageResourceRepository,
    private val s3Uploader: com.unifor.backend.common.s3.S3Uploader
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

    // Admin only endpoint (In a real app, secure this with PreAuthorize("hasRole('ADMIN')"))
    @GetMapping("/all")
    fun getAllGames(): ResponseEntity<List<GameSummaryDTO>> {
        val games = gameService.getAllGames()
        return ResponseEntity.ok(games)
    }

    @GetMapping("/s3/{gameId}")
    fun getGameThumbnail(
        @PathVariable gameId: String,
        @RequestParam(required = false, defaultValue = "thumbnail") imageType: String
    ): ResponseEntity<Void> {
        val imageResource = imageResourceRepository.findByOwnerTypeAndOwnerIdAndImageTypeAndIsActive(
            ownerType = "GAME",
            ownerId = gameId,
            imageType = imageType,
            isActive = true
        ) ?: return ResponseEntity.notFound().build()

        val presignedUrl = s3Uploader.getDownloadUrl(imageResource.s3Key)
        return ResponseEntity.status(org.springframework.http.HttpStatus.FOUND)
            .header(org.springframework.http.HttpHeaders.LOCATION, presignedUrl)
            .build()
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

    @PatchMapping("/{gameId}")
    fun updateGame(
        @PathVariable gameId: String,
        @RequestBody request: UpdateGameRequest
    ): ResponseEntity<GameSummaryDTO> {
        return try {
            val updated = gameService.updateGame(gameId, request.title, request.description, request.thumbnailUrl, request.isPublic)
            ResponseEntity.ok(updated)
        } catch (e: jakarta.persistence.EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{gameId}")
    fun deleteGame(@PathVariable gameId: String): ResponseEntity<Void> {
        return try {
            gameService.deleteGame(gameId)
            ResponseEntity.noContent().build()
        } catch (e: jakarta.persistence.EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/all")
    fun deleteAllGames(): ResponseEntity<Void> {
        gameService.deleteAllGames()
        return ResponseEntity.noContent().build()
    }
}

data class UpdateGameRequest(
    val title: String? = null,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val isPublic: Boolean? = null
)

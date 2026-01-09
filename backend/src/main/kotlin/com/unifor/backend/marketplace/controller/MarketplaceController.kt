package com.unifor.backend.marketplace.controller

import com.unifor.backend.repository.GameRepository
import com.unifor.backend.repository.GameVersionRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MarketplaceController(
    private val gameRepository: GameRepository,
    private val gameVersionRepository: GameVersionRepository
) {

    @GetMapping("/marketplace/games")
    fun listGames(): List<Map<String, Any?>> {
        return gameRepository.findAll().map { game ->
            val versions = gameVersionRepository.findByGameIdOrderByVersionNumberDesc(game.id)
            val latestVersion = versions.firstOrNull()

            mapOf(
                "gameId" to game.id,
                "title" to game.title,
                "s3RootPath" to null // Legacy GameVersion does not have s3RootPath
            )
        }
    }
}

package com.unifor.backend.marketplace.controller

import com.unifor.backend.game.repository.GameRepository
import com.unifor.backend.game.repository.GameVersionRepository
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
            val version = gameVersionRepository
                .findTopByGameIdAndStatusOrderByCreatedAtDesc(
                    game.id,
                    "PUBLISHED"
                )

            mapOf(
                "gameId" to game.id,
                "title" to game.title,
                "s3RootPath" to version?.s3RootPath
            )
        }
    }
}




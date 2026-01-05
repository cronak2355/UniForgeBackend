package com.unifor.backend.game.service

import com.unifor.backend.game.entity.Game
import com.unifor.backend.game.repository.GameRepository
import org.springframework.stereotype.Service

@Service
class GameService(
    private val gameRepository: GameRepository
) {
    fun createGame(authorId: String, title: String, description: String?): Game {
        return gameRepository.save(
            Game(
                authorId = authorId,
                title = title,
                description = description
            )
        )
    }
}




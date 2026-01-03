package com.uniforge.backend.game.service

import com.uniforge.backend.game.entity.Game
import com.uniforge.backend.game.repository.GameRepository
import org.springframework.stereotype.Service

@Service
class GameService(
    private val gameRepository: GameRepository
) {
    fun createGame(authorId: Long, title: String, description: String?): Game {
        return gameRepository.save(
            Game(
                authorId = authorId,
                title = title,
                description = description
            )
        )
    }
}

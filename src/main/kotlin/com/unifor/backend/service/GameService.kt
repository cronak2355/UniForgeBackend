package com.unifor.backend.service

import com.unifor.backend.dto.GameSummaryDTO
import com.unifor.backend.entity.Game
import com.unifor.backend.entity.GameVersion
import com.unifor.backend.repository.GameRepository
import com.unifor.backend.repository.GameVersionRepository
import com.unifor.backend.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GameService(
    private val gameRepository: GameRepository,
    private val gameVersionRepository: GameVersionRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun createGame(authorId: String, title: String, description: String?): GameSummaryDTO {
        val user = userRepository.findById(authorId)
            .orElseThrow { EntityNotFoundException("User not found with id $authorId") }

        val game = Game(
            title = title,
            description = description,
            author = user
        )
        val savedGame = gameRepository.save(game)
        return GameSummaryDTO.from(savedGame)
    }

    @Transactional(readOnly = true)
    fun getMyGames(authorId: String): List<GameSummaryDTO> {
        return gameRepository.findByAuthorId(authorId)
            .map { GameSummaryDTO.from(it) }
    }

    @Transactional(readOnly = true)
    fun getPublicGames(): List<GameSummaryDTO> {
        return gameRepository.findAll()
            .map { GameSummaryDTO.from(it) }
    }

    @Transactional
    fun saveGameVersion(gameId: Long, sceneJson: String) {
        val game = gameRepository.findById(gameId)
            .orElseThrow { EntityNotFoundException("Game not found with id $gameId") }

        // Find latest version to increment logic
        val versions = gameVersionRepository.findByGameIdOrderByVersionNumberDesc(gameId)
        val nextVersionNumber = if (versions.isEmpty()) 1 else versions[0].versionNumber + 1

        val version = GameVersion(
            game = game,
            versionNumber = nextVersionNumber,
            sceneJson = sceneJson
        )
        gameVersionRepository.save(version)
    }
}

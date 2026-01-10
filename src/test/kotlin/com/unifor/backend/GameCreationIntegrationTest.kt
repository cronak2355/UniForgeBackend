package com.unifor.backend

import com.unifor.backend.entity.AuthProvider
import com.unifor.backend.entity.User
import com.unifor.backend.repository.GameRepository
import com.unifor.backend.repository.UserRepository
import com.unifor.backend.service.GameService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@org.springframework.boot.autoconfigure.EnableAutoConfiguration(exclude = [
    org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration::class, 
    org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration::class
])
@ActiveProfiles("test")
class GameCreationIntegrationTest {

    @org.springframework.boot.test.mock.mockito.MockBean
    lateinit var s3Client: software.amazon.awssdk.services.s3.S3Client

    @org.springframework.boot.test.mock.mockito.MockBean
    lateinit var redisConnectionFactory: org.springframework.data.redis.connection.RedisConnectionFactory

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var gameService: GameService

    @Autowired
    lateinit var gameRepository: GameRepository

    @Test
    fun `should create user and game with UUIDs successfully`() {
        // 1. Create a User with UUID
        val user = User(
            email = "test@example.com",
            name = "Test User",
            provider = AuthProvider.LOCAL,
            password = "password"
        )
        // Ensure ID is UUID format (it has default value, but verifying)
        assertNotNull(UUID.fromString(user.id))
        
        val savedUser = userRepository.save(user)
        println("Saved User ID: ${savedUser.id}")

        // 2. Create a Game linked to this User
        // Service internally looks up user by String ID
        val gameDTO = gameService.createGame(
            authorId = savedUser.id,
            title = "Integration Test Game",
            description = "Testing UUID support"
        )

        // 3. Verify
        println("Created Game ID: ${gameDTO.gameId}")
        assertNotNull(UUID.fromString(gameDTO.gameId))
        assertEquals(savedUser.id, gameDTO.authorId)

        val fetchedGame = gameRepository.findById(gameDTO.gameId)
        assert(fetchedGame.isPresent)
        assertEquals("Integration Test Game", fetchedGame.get().title)
    }
}

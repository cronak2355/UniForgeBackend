package com.unifor.backend.repository

import com.unifor.backend.entity.Game
import org.springframework.data.jpa.repository.JpaRepository

interface GameRepository : JpaRepository<Game, String> {
    fun findByAuthor_Id(authorId: String): List<Game>
}

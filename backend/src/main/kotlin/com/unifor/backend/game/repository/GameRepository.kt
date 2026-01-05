package com.unifor.backend.game.repository

import com.unifor.backend.game.entity.Game
import org.springframework.data.jpa.repository.JpaRepository

interface GameRepository : JpaRepository<Game, Long>

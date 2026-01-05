package com.uniforge.backend.game.repository

import com.uniforge.backend.game.entity.Game
import org.springframework.data.jpa.repository.JpaRepository

interface GameRepository : JpaRepository<Game, Long>

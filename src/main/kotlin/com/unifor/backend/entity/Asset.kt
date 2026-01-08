package com.unifor.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "assets")
data class Asset(
    @Id
    val id: String = UUID.randomUUID().toString(),
    
    @Column(nullable = false)
    val name: String,
    
    @Column(nullable = false)
    val price: BigDecimal = BigDecimal.ZERO,
    
    @Column(length = 2000)
    val description: String? = null,
    
    @Column(nullable = false)
    val authorId: String,
    
    @Column(nullable = true)
    val imageUrl: String? = null,
    
    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    val isPublic: Boolean = true,

    @Column(nullable = false)
    val genre: String = "Other"
)




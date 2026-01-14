package com.unifor.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "assets")
data class Asset(
    @Id
    @Column(length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var price: BigDecimal = BigDecimal.ZERO,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false)
    val authorId: String,

    @Column(nullable = false)
    var isPublic: Boolean = true,

    @Column(length = 50)
    var genre: String? = null,

    @Column(columnDefinition = "TEXT")
    var tags: String? = null,

    @Column(length = 50)
    var assetType: String? = "오브젝트",

    @Column(length = 2048)
    var imageUrl: String? = null,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
)

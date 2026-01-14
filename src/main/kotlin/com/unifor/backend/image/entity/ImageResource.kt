package com.unifor.backend.image.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "image_resources")
data class ImageResource(
    @Id
    @Column(length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(nullable = false)
    val ownerType: String, // ASSET, GAME, USER

    @Column(nullable = false)
    val ownerId: String,

    @Column(nullable = false)
    val imageType: String, // main, thumbnail, preview

    @Column(name = "s3_key", nullable = false)
    val s3Key: String,

    @Column(nullable = false)
    val isActive: Boolean = true,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
)

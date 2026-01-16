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

    @Column(name = "owner_type", nullable = false)
    val ownerType: String, // ASSET, GAME, USER

    @Column(name = "owner_id", nullable = false)
    val ownerId: String,

    @Column(name = "image_type", nullable = false)
    val imageType: String, // main, thumbnail, preview

    @Column(name = "s3_key", nullable = false, length = 512)
    val s3Key: String,

    // Legacy column support removed

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)

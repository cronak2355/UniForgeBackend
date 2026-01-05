package com.unifor.backend.image.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "image_resource")
class ImageResource(

    @Id
    val id: String = java.util.UUID.randomUUID().toString(),

    @Column(name = "owner_type", nullable = false)
    val ownerType: String,

    @Column(name = "owner_id", nullable = false)
    val ownerId: String,

    @Column(name = "image_type", nullable = false)
    val imageType: String,

    @Column(name = "s3_key", nullable = false, unique = true)
    val s3Key: String,

    @Column(name = "content_type")
    val contentType: String?,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)




package com.uniforge.backend.image.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "image_resource")
class ImageResource(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "owner_type", nullable = false)
    val ownerType: String,

    @Column(name = "owner_id", nullable = false)
    val ownerId: Long,

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

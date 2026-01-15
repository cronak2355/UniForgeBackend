package com.unifor.backend.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "asset_versions")
data class AssetVersion(
    @Id
    @Column(length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(nullable = false)
    val assetId: String,

    @Column(nullable = false)
    val version: String = "1.0.0",

    @Column(length = 255)
    val s3RootPath: String? = null,

    @Column(nullable = false)
    var status: String = "DRAFT", // DRAFT, PUBLISHED

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = true)
    var updatedAt: Instant? = null
)

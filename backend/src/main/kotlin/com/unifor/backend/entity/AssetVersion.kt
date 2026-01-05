package com.unifor.backend.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "asset_versions")
data class AssetVersion(
    @Id
    val id: String = UUID.randomUUID().toString(),
    
    @Column(nullable = false)
    val assetId: String,
    
    @Column(nullable = true)
    var s3RootPath: String? = null,
    
    @Column(nullable = false)
    var status: String = "PENDING",
    
    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
)

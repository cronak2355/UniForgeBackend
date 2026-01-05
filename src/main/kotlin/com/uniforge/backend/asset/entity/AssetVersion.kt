package com.uniforge.backend.asset.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "asset_versions")
class AssetVersion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    val asset: Asset,

    @Column(name = "s3_root_path", nullable = false)
    val s3RootPath: String,

    @Column(nullable = false)
    val status: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

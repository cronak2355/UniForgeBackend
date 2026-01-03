package com.uniforge.backend.purchase.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "purchases")
class Purchase(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "asset_version_id", nullable = false)
    val assetVersionId: Long,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

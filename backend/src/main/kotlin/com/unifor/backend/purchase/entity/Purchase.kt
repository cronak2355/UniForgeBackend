package com.unifor.backend.purchase.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "purchases")
class Purchase(
    @Id
    val id: String = java.util.UUID.randomUUID().toString(),

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "asset_version_id", nullable = false)
    val assetVersionId: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)




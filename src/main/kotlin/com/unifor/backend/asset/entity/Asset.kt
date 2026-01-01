package com.uniforge.backend.asset.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "assets")
class Asset(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "author_id", nullable = false)
    val authorId: Long,

    @Column(nullable = false)
    val name: String,

    val description: String? = null,

    @Column(nullable = false)
    val price: Int = 0,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

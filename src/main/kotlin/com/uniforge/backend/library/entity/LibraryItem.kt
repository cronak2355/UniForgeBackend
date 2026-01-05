package com.uniforge.backend.library.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "library_items")
class LibraryItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "item_type", nullable = false)
    val itemType: String, // GAME, ASSET

    @Column(name = "ref_id", nullable = false)
    val refId: Long,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

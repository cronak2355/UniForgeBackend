package com.unifor.backend.library.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "library_items")
class LibraryItem(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "item_type", nullable = false)
    val itemType: String, // GAME, ASSET

    @Column(name = "ref_id", nullable = false)
    val refId: String,

    @Column(name = "collection_id")
    var collectionId: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)


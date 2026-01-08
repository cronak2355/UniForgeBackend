package com.unifor.backend.library.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "library_collections")
class LibraryCollection(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

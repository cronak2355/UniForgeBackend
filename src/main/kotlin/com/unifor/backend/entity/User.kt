package com.unifor.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(length = 36)
    val id: String, // UUID passed from auth provider or generated

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(nullable = true)
    var profileImage: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.USER,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
)

enum class Role {
    USER, ADMIN
}

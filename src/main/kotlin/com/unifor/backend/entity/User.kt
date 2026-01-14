package com.unifor.backend.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(nullable = true)
    var password: String? = null,

    @Column(nullable = true)
    var profileImage: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.USER,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var provider: AuthProvider = AuthProvider.LOCAL,

    @Column(length = 255)
    var providerId: String? = null,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
)

enum class UserRole {
    USER, ADMIN
}

enum class AuthProvider {
    LOCAL, GOOGLE
}

package com.unifor.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: String = java.util.UUID.randomUUID().toString(),
    
    @Column(unique = true, nullable = false)
    val email: String,
    
    @Column(nullable = true)
    var password: String? = null,
    
    @Column(nullable = false)
    var name: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: AuthProvider = AuthProvider.LOCAL,
    
    @Column(nullable = true)
    val providerId: String? = null,
    
    @Column(nullable = true)
    var profileImage: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.USER,
    
    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
)

enum class AuthProvider {
    LOCAL, GOOGLE
}

enum class UserRole {
    USER, ADMIN
}

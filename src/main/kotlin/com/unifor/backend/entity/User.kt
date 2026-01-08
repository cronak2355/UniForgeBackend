<<<<<<< HEAD
package com.unifor.backend.entity
=======
﻿package com.unifor.backend.entity
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
data class User(
    @Id
<<<<<<< HEAD
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
=======
    val id: String = java.util.UUID.randomUUID().toString(),
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
    
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
    
    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
)

enum class AuthProvider {
    LOCAL, GOOGLE
}

<<<<<<< HEAD
=======



>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

package com.unifor.backend.controller

import com.unifor.backend.entity.UserRole
import com.unifor.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SystemController(
    @Value("\${COMMIT_HASH:unknown}")
    private val commitHash: String,
    private val userRepository: UserRepository
) {
    @GetMapping("/system/version")
    fun getVersion(): Map<String, String> {
        return mapOf(
            "commitHash" to commitHash,
            "buildTime" to (System.getenv("BUILD_TIME") ?: "unknown")
        )
    }

    @GetMapping("/system/promote")
    fun promoteToAdmin(@RequestParam email: String): ResponseEntity<String> {
        val user = userRepository.findByEmail(email) 
            ?: return ResponseEntity.status(404).body("User with email '$email' not found")
            
        if (user.role == UserRole.ADMIN) {
             return ResponseEntity.ok("User '${user.name}' ($email) is already ADMIN")
        }

        user.role = UserRole.ADMIN
        userRepository.save(user)
        return ResponseEntity.ok("User '${user.name}' ($email) has been successfully promoted to ADMIN")
    }
}

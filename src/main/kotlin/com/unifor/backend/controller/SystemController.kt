package com.unifor.backend.controller

import com.unifor.backend.entity.UserRole
import com.unifor.backend.repository.UserRepository
import com.unifor.backend.library.repository.LibraryItemRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SystemController(
    @Value("\${COMMIT_HASH:unknown}")
    private val commitHash: String,
    private val userRepository: UserRepository,
    private val libraryItemRepository: LibraryItemRepository
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

    @GetMapping("/system/library-debug")
    fun debugLibrary(@RequestParam email: String): ResponseEntity<Any> {
        val user = userRepository.findByEmail(email) 
            ?: return ResponseEntity.status(404).body("User not found")
            
        val items = libraryItemRepository.findByUserId(user.id)
        
        val response = items.map { item ->
            mapOf(
                "id" to item.id,
                "userId" to item.userId,
                "targetId" to item.targetId,
                "targetType" to item.targetType,
                "createdAt" to item.createdAt.toString()
            )
        }
        return ResponseEntity.ok(response)
    }
}

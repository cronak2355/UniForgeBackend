package com.unifor.backend.controller

import com.unifor.backend.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepository: UserRepository
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): ResponseEntity<Any> {
        val user = userRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        val response = mapOf(
            "id" to user.id,
            "name" to user.name,
            "email" to user.email,
            "profileImage" to user.profileImage,
            "role" to user.role.name
        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/ids")
    fun getUsersByIds(@RequestBody ids: List<String>): ResponseEntity<Any> {
        val users = userRepository.findAllById(ids)
        val response = users.associate { user ->
            user.id to mapOf(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email,
                "profileImage" to user.profileImage
            )
        }
        return ResponseEntity.ok(response)
    }
}

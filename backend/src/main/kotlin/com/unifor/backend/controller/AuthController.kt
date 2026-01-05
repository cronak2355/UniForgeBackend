package com.unifor.backend.controller

import com.unifor.backend.dto.*
import com.unifor.backend.entity.User
import com.unifor.backend.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    
    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: SignupRequest): ResponseEntity<AuthResponse> {
        val response = authService.signup(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal user: com.unifor.backend.security.UserPrincipal): ResponseEntity<UserDTO> {
        val userDto = authService.getCurrentUser(user.id)
        return ResponseEntity.ok(userDto)
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest().body(
            ErrorResponse(message = e.message ?: "?˜ëª»???”ì²­?…ë‹ˆ??)
        )
    }
}

package com.unifor.backend.controller

import com.unifor.backend.dto.*
import com.unifor.backend.security.UserPrincipal
import com.unifor.backend.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.signup(request))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.login(request))
    }

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal userPrincipal: UserPrincipal): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(authService.getCurrentUser(userPrincipal.id))
    }
}

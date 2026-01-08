<<<<<<< HEAD
package com.unifor.backend.controller
=======
﻿package com.unifor.backend.controller
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

import com.unifor.backend.dto.*
import com.unifor.backend.entity.User
import com.unifor.backend.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
<<<<<<< HEAD
=======
import com.unifor.backend.security.UserPrincipal
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
<<<<<<< HEAD
@RequestMapping("/api/auth")
=======
@RequestMapping("/auth")
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
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
<<<<<<< HEAD
    fun getCurrentUser(@AuthenticationPrincipal user: User): ResponseEntity<UserDTO> {
=======
    fun getCurrentUser(@AuthenticationPrincipal user: UserPrincipal): ResponseEntity<UserDTO> {
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
        val userDto = authService.getCurrentUser(user.id)
        return ResponseEntity.ok(userDto)
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest().body(
            ErrorResponse(message = e.message ?: "잘못된 요청입니다")
        )
    }
}
<<<<<<< HEAD
=======



>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

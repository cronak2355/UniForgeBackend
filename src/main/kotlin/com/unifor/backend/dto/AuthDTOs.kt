package com.unifor.backend.dto

import com.unifor.backend.entity.AuthProvider
import com.unifor.backend.entity.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// 회원가입 요청
data class SignupRequest(
    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String,
    
    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    val password: String,
    
    @field:NotBlank(message = "이름은 필수입니다")
    val name: String
)

// 로그인 요청
data class LoginRequest(
    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String,
    
    @field:NotBlank(message = "비밀번호는 필수입니다")
    val password: String
)

// 인증 응답
data class AuthResponse(
    val token: String,
    val user: UserDTO
)

// 사용자 정보 DTO
data class UserDTO(
    val id: Long,
    val email: String,
    val name: String,
    val provider: AuthProvider,
    val profileImage: String?
) {
    companion object {
        fun from(user: User): UserDTO = UserDTO(
            id = user.id,
            email = user.email,
            name = user.name,
            provider = user.provider,
            profileImage = user.profileImage
        )
    }
}

// 에러 응답
data class ErrorResponse(
    val message: String,
    val code: String? = null
)

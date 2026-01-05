package com.unifor.backend.dto

import com.unifor.backend.entity.AuthProvider
import com.unifor.backend.entity.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// ?Œì›ê°€???”ì²­
data class SignupRequest(
    @field:NotBlank(message = "?´ë©”?¼ì? ?„ìˆ˜?…ë‹ˆ??)
    @field:Email(message = "?¬ë°”ë¥??´ë©”???•ì‹???„ë‹™?ˆë‹¤")
    val email: String,
    
    @field:NotBlank(message = "ë¹„ë?ë²ˆí˜¸???„ìˆ˜?…ë‹ˆ??)
    @field:Size(min = 6, message = "ë¹„ë?ë²ˆí˜¸??ìµœì†Œ 6???´ìƒ?´ì–´???©ë‹ˆ??)
    val password: String,
    
    @field:NotBlank(message = "?´ë¦„?€ ?„ìˆ˜?…ë‹ˆ??)
    val name: String
)

// ë¡œê·¸???”ì²­
data class LoginRequest(
    @field:NotBlank(message = "?´ë©”?¼ì? ?„ìˆ˜?…ë‹ˆ??)
    @field:Email(message = "?¬ë°”ë¥??´ë©”???•ì‹???„ë‹™?ˆë‹¤")
    val email: String,
    
    @field:NotBlank(message = "ë¹„ë?ë²ˆí˜¸???„ìˆ˜?…ë‹ˆ??)
    val password: String
)

// ?¸ì¦ ?‘ë‹µ
data class AuthResponse(
    val token: String,
    val user: UserDTO
)

// ?¬ìš©???•ë³´ DTO
data class UserDTO(
    val id: String,
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

// ?ëŸ¬ ?‘ë‹µ
data class ErrorResponse(
    val message: String,
    val code: String? = null
)

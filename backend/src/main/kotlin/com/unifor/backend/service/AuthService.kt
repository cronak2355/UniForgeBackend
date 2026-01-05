package com.unifor.backend.service

import com.unifor.backend.dto.*
import com.unifor.backend.entity.AuthProvider
import com.unifor.backend.entity.User
import com.unifor.backend.repository.UserRepository
import com.unifor.backend.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    
    @Transactional
    fun signup(request: SignupRequest): AuthResponse {
        // ?´ë©”??ì¤‘ë³µ ì²´í¬
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("?´ë? ?¬ìš© ì¤‘ì¸ ?´ë©”?¼ì…?ˆë‹¤")
        }
        
        // ?¬ìš©???ì„±
        val user = userRepository.save(
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                name = request.name,
                provider = AuthProvider.LOCAL
            )
        )
        
        // JWT ? í° ?ì„±
        val token = jwtTokenProvider.generateToken(user.id, user.email)
        
        return AuthResponse(
            token = token,
            user = UserDTO.from(user)
        )
    }
    
    fun login(request: LoginRequest): AuthResponse {
        // ?¬ìš©??ì¡°íšŒ
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { IllegalArgumentException("?´ë©”???ëŠ” ë¹„ë?ë²ˆí˜¸ê°€ ?¬ë°”ë¥´ì? ?ŠìŠµ?ˆë‹¤") }
        
        // ë¹„ë?ë²ˆí˜¸ ?•ì¸
        if (user.password == null || !passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("?´ë©”???ëŠ” ë¹„ë?ë²ˆí˜¸ê°€ ?¬ë°”ë¥´ì? ?ŠìŠµ?ˆë‹¤")
        }
        
        // OAuth ?¬ìš©?ê? ë¡œì»¬ ë¡œê·¸???œë„
        if (user.provider != AuthProvider.LOCAL) {
            throw IllegalArgumentException("${user.provider.name} ê³„ì •?¼ë¡œ ë¡œê·¸?¸í•´ì£¼ì„¸??)
        }
        
        // JWT ? í° ?ì„±
        val token = jwtTokenProvider.generateToken(user.id, user.email)
        
        return AuthResponse(
            token = token,
            user = UserDTO.from(user)
        )
    }
    
    fun getCurrentUser(userId: String): UserDTO {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("?¬ìš©?ë? ì°¾ì„ ???†ìŠµ?ˆë‹¤") }
        
        return UserDTO.from(user)
    }
}

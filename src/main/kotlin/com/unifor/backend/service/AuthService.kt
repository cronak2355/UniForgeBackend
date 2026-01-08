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
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("이미 사용 중인 이메일입니다")
        }
        
        // 사용자 생성
        val user = userRepository.save(
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                name = request.name,
                provider = AuthProvider.LOCAL
            )
        )
        
        // JWT 토큰 생성
        val token = jwtTokenProvider.generateToken(user.id, user.email)
        
        return AuthResponse(
            token = token,
            user = UserDTO.from(user)
        )
    }
    
    fun login(request: LoginRequest): AuthResponse {
        // 사용자 조회
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다") }
        
        // 비밀번호 확인
        if (user.password == null || !passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다")
        }
        
        // OAuth 사용자가 로컬 로그인 시도
        if (user.provider != AuthProvider.LOCAL) {
            throw IllegalArgumentException("${user.provider.name} 계정으로 로그인해주세요")
        }
        
        // JWT 토큰 생성
        val token = jwtTokenProvider.generateToken(user.id, user.email)
        
        return AuthResponse(
            token = token,
            user = UserDTO.from(user)
        )
    }
    
    fun getCurrentUser(userId: String): UserDTO {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다") }
        
        return UserDTO.from(user)
    }
}

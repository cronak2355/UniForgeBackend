package com.unifor.backend.security

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val jwtSecret: String,
    
    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long
) {
    
    private val key: SecretKey by lazy {
        val keyBytes = try {
            // Base64 인코딩된 키 시도
            Decoders.BASE64.decode(jwtSecret)
        } catch (e: Exception) {
            // Base64가 아니면 직접 바이트 배열로 변환
            if (jwtSecret.length < 32) {
                // 비밀키가 짧으면 패딩
                jwtSecret.padEnd(32, '0').toByteArray()
            } else {
                jwtSecret.toByteArray()
            }
        }
        Keys.hmacShaKeyFor(keyBytes)
    }
    
    fun generateToken(userId: Long, email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpiration)
        
        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }
    
    fun getUserIdFromToken(token: String): Long {
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
        
        return claims.subject.toLong()
    }
    
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}

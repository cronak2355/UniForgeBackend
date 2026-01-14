package com.unifor.backend.security

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
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
    private val log = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    
    private val key: SecretKey by lazy {
        val secretBytes = jwtSecret.toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        // Ensure strictly secure key (HMAC-SHA requires 256 bits / 32 bytes)
        val secureKeyBytes = if (secretBytes.size < 32) {
             jwtSecret.padEnd(32, '0').toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        } else {
             secretBytes
        }
        Keys.hmacShaKeyFor(secureKeyBytes)
    }
    
    fun generateToken(userId: String, email: String, role: String = "USER"): String {
        log.info("Generating token for user: {}", userId)
        val now = Date()
        val expiryDate = Date(now.time + jwtExpiration)
        
        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }
    
    fun getUserIdFromToken(token: String): String {
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
        
        return claims.subject
    }
    
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            true
        } catch (e: SecurityException) {
            log.error("Invalid JWT signature: ${e.message}")
            false
        } catch (e: MalformedJwtException) {
            log.error("Invalid JWT token: ${e.message}")
            false
        } catch (e: ExpiredJwtException) {
            log.error("JWT token is expired: ${e.message}")
            false
        } catch (e: UnsupportedJwtException) {
            log.error("JWT token is unsupported: ${e.message}")
            false
        } catch (e: IllegalArgumentException) {
            log.error("JWT claims string is empty: ${e.message}")
            false
        } catch (e: Exception) {
            log.info("Error validating JWT: ${e.message}")
            false
        }
    }
}

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
        log.info("Initializing JWT secret key. Secret length: ${jwtSecret.length}")
        val keyBytes = try {
            if (jwtSecret.contains(" ") || jwtSecret.length < 32) {
                 jwtSecret.padEnd(32, '0').toByteArray()
            } else {
                Decoders.BASE64.decode(jwtSecret)
            }
        } catch (e: Exception) {
            log.debug("Secret is not Base64, using raw bytes: ${e.message}")
            jwtSecret.padEnd(32, '0').toByteArray()
        }
        Keys.hmacShaKeyFor(keyBytes)
    }
    
    fun generateToken(userId: String, email: String): String {
        log.info("Generating token for user: {}", userId)
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




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
            // Base64 ?∏ÏΩî?©Îêú ???úÎèÑ
            Decoders.BASE64.decode(jwtSecret)
        } catch (e: Exception) {
            // Base64Í∞Ä ?ÑÎãàÎ©?ÏßÅÏ†ë Î∞îÏù¥??Î∞∞Ïó¥Î°?Î≥Ä??
            if (jwtSecret.length < 32) {
                // ÎπÑÎ??§Í? ÏßßÏúºÎ©??®Îî©
                jwtSecret.padEnd(32, '0').toByteArray()
            } else {
                jwtSecret.toByteArray()
            }
        }
        Keys.hmacShaKeyFor(keyBytes)
    }
    
    fun generateToken(userId: String, email: String): String {
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
        } catch (e: Exception) {
            false
        }
    }
}

<<<<<<< HEAD
package com.unifor.backend.security
=======
﻿package com.unifor.backend.security
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
<<<<<<< HEAD
=======
import org.slf4j.LoggerFactory
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
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
<<<<<<< HEAD
    
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
=======
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
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
        }
        Keys.hmacShaKeyFor(keyBytes)
    }
    
<<<<<<< HEAD
    fun generateToken(userId: Long, email: String): String {
=======
    fun generateToken(userId: String, email: String): String {
        log.info("Generating token for user: {}", userId)
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
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
    
<<<<<<< HEAD
    fun getUserIdFromToken(token: String): Long {
=======
    fun getUserIdFromToken(token: String): String {
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
        
<<<<<<< HEAD
        return claims.subject.toLong()
=======
        return claims.subject
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
    }
    
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
<<<<<<< HEAD
        } catch (e: Exception) {
=======
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
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
            false
        }
    }
}
<<<<<<< HEAD
=======



>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

<<<<<<< HEAD
package com.unifor.backend.security
=======
﻿package com.unifor.backend.security
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

import com.unifor.backend.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
<<<<<<< HEAD
=======
import org.slf4j.LoggerFactory
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {
    
<<<<<<< HEAD
=======
    private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
<<<<<<< HEAD
            val jwt = getJwtFromRequest(request)
            
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                val userId = jwtTokenProvider.getUserIdFromToken(jwt)
                val user = userRepository.findById(userId).orElse(null)
                
                if (user != null) {
                    val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                    val authentication = UsernamePasswordAuthenticationToken(
                        user, null, authorities
                    )
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (e: Exception) {
            logger.error("JWT 인증 필터 오류: ${e.message}")
=======
            val authHeader = request.getHeader("Authorization")
            val jwt = getJwtFromRequest(request)
            println("Antigravity_Debug: Request path: ${request.requestURI}, Auth header: ${if (authHeader != null) "Present (${authHeader.take(15)}...)" else "Missing"}, JWT extracted: ${if (jwt != null) "Present" else "Missing"}")
            
            if (jwt != null) {
                if (jwtTokenProvider.validateToken(jwt)) {
                    val userId = jwtTokenProvider.getUserIdFromToken(jwt)
                    val user = userRepository.findById(userId).orElse(null)
                    
                    if (user != null) {
                        val userPrincipal = UserPrincipal.create(user)
                        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                        val authentication = UsernamePasswordAuthenticationToken(
                            userPrincipal, null, authorities
                        )
                        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authentication
                        println("Antigravity_Debug: User authenticated: $userId")
                    } else {
                        println("Antigravity_Debug: User from JWT not found in DB: $userId")
                    }
                } else {
                    println("Antigravity_Debug: Invalid JWT token detected during validation")
                }
            }
        } catch (e: Exception) {
            println("Antigravity_Debug: JWT 인증 필터 오류: ${e.message}")
            e.printStackTrace()
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
        }
        
        filterChain.doFilter(request, response)
    }
    
    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
}
<<<<<<< HEAD
=======



>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

package com.unifor.backend.security

import com.unifor.backend.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = getJwtFromRequest(request)
            
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                val userId = jwtTokenProvider.getUserIdFromToken(jwt)
                val user = userRepository.findById(userId).orElse(null)
                
                if (user != null) {
                    // UserPrincipalÎ°?Í∞êÏã∏???§Ï†ï
                    val userPrincipal = UserPrincipal.create(user)
                    val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                    val authentication = UsernamePasswordAuthenticationToken(
                        userPrincipal, null, authorities
                    )
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (e: Exception) {
            logger.error("JWT ?∏Ï¶ù ?ÑÌÑ∞ ?§Î•ò: ${e.message}")
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

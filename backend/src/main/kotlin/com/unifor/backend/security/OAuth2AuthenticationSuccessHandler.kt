package com.unifor.backend.security

import com.unifor.backend.entity.AuthProvider
import com.unifor.backend.entity.User
import com.unifor.backend.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
    @Value("\${app.oauth2.redirect-uri:https://uniforge.kr}")
    private val redirectUri: String
) : SimpleUrlAuthenticationSuccessHandler() {
    
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val attributes = oAuth2User.attributes
        
        val email = attributes["email"] as String
        val name = attributes["name"] as? String ?: email.substringBefore("@")
        val providerId = attributes["sub"] as String
        val picture = attributes["picture"] as? String
        
        // ?¬ìš©??ì¡°íšŒ ?ëŠ” ?ì„±
        val user = userRepository.findByEmail(email).orElseGet {
            userRepository.save(
                User(
                    email = email,
                    name = name,
                    provider = AuthProvider.GOOGLE,
                    providerId = providerId,
                    profileImage = picture
                )
            )
        }.also { existingUser ->
            // ê¸°ì¡´ ?¬ìš©?ì˜ ?„ë¡œ???´ë?ì§€ ?…ë°?´íŠ¸
            if (existingUser.profileImage != picture && picture != null) {
                existingUser.profileImage = picture
                userRepository.save(existingUser)
            }
        }
        
        // JWT ? í° ?ì„±
        val token = jwtTokenProvider.generateToken(user.id, user.email)
        
        // ?„ë¡ ?¸ì—”?œë¡œ ë¦¬ë‹¤?´ë ‰??(? í° ?¬í•¨)
        val targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
            .path("/oauth/callback")
            .queryParam("token", token)
            .build()
            .toUriString()
        
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}

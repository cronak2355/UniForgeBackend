package com.unifor.backend.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class OAuth2AuthenticationFailureHandler(
    @Value("\${app.oauth2.redirect-uri:https://uniforge.kr}")
    private val redirectUri: String
) : SimpleUrlAuthenticationFailureHandler() {

    private val log = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler::class.java)

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        log.error("OAuth2 인증 실패: {}", exception.message, exception)
        
        // 실제 에러 메시지를 URL에 포함
        val errorMessage = exception.message 
            ?: exception.cause?.message 
            ?: "social_login_failed"
        
        val encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8)
        
        val targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
            .path("/auth")
            .queryParam("error", encodedError)
            .build()
            .toUriString()

        log.info("OAuth2 실패 리다이렉트: {}", targetUrl)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}

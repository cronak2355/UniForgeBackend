<<<<<<< HEAD
package com.unifor.backend.security
=======
﻿package com.unifor.backend.security
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

import com.unifor.backend.entity.AuthProvider
import com.unifor.backend.entity.User
import com.unifor.backend.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
<<<<<<< HEAD
=======
import org.slf4j.LoggerFactory
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
<<<<<<< HEAD
=======
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
    @Value("\${app.oauth2.redirect-uri:https://uniforge.kr}")
<<<<<<< HEAD
    private val redirectUri: String
) : SimpleUrlAuthenticationSuccessHandler() {
    
=======
    private val redirectUri: String,
    private val httpCookieOAuth2AuthorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository
) : SimpleUrlAuthenticationSuccessHandler() {
    
    private val log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler::class.java)
    
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
<<<<<<< HEAD
        val oAuth2User = authentication.principal as OAuth2User
        val attributes = oAuth2User.attributes
        
        val email = attributes["email"] as String
        val name = attributes["name"] as? String ?: email.substringBefore("@")
        val providerId = attributes["sub"] as String
        val picture = attributes["picture"] as? String
        
        // 사용자 조회 또는 생성
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
            // 기존 사용자의 프로필 이미지 업데이트
            if (existingUser.profileImage != picture && picture != null) {
                existingUser.profileImage = picture
                userRepository.save(existingUser)
            }
        }
        
        // JWT 토큰 생성
        val token = jwtTokenProvider.generateToken(user.id, user.email)
        
        // 프론트엔드로 리다이렉트 (토큰 포함)
        val targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
            .path("/oauth/callback")
            .queryParam("token", token)
            .build()
            .toUriString()
        
        redirectStrategy.sendRedirect(request, response, targetUrl)
=======
        val targetUrl = determineTargetUrl(request, response, authentication)

        if (response.isCommitted) {
            log.debug("응답이 이미 커밋되었습니다. 리다이렉트 할 수 없습니다: {}", targetUrl)
            return
        }

        try {
            val oAuth2User = authentication.principal as OAuth2User
            val attributes = oAuth2User.attributes
            
            log.debug("OAuth2 로그인 시도: attributes = {}", attributes)
            
            val email = attributes["email"] as? String 
                ?: throw IllegalStateException("이메일 정보를 가져올 수 없습니다")
            val name = attributes["name"] as? String ?: email.substringBefore("@")
            val providerId = attributes["sub"] as? String 
                ?: throw IllegalStateException("Provider ID를 가져올 수 없습니다")
            val picture = attributes["picture"] as? String
            
            log.info("OAuth2 로그인: email={}, name={}", email, name)
            
            // 사용자 조회 또는 생성
            val existingUser = userRepository.findByEmail(email).orElse(null)
            val user = if (existingUser != null) {
                log.debug("기존 사용자 발견: id={}", existingUser.id)
                // 기존 사용자: 프로필 이미지만 업데이트 (필요시)
                if (existingUser.profileImage != picture && picture != null) {
                    existingUser.profileImage = picture
                    userRepository.save(existingUser)
                } else {
                    existingUser
                }
            } else {
                log.info("신규 사용자 생성: email={}", email)
                // 신규 사용자 생성
                userRepository.save(
                    User(
                        email = email,
                        name = name,
                        provider = AuthProvider.GOOGLE,
                        providerId = providerId,
                        profileImage = picture
                    )
                )
            }
            
            // JWT 토큰 생성
            val token = jwtTokenProvider.generateToken(user.id, user.email)
            log.debug("JWT 토큰 생성 완료: userId={}", user.id)
            
            // 프론트엔드로 리다이렉트 (토큰 포함)
            val redirectUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .path("/oauth/callback")
                .queryParam("token", token)
                .build()
                .toUriString()
            
            log.info("OAuth2 로그인 성공, 리다이렉트: {}", redirectUrl)
            
            clearAuthenticationAttributes(request, response)
            redirectStrategy.sendRedirect(request, response, redirectUrl)
            
        } catch (e: Exception) {
            log.error("OAuth2 로그인 처리 중 오류 발생", e)
            
            // 에러 발생 시 프론트엔드로 에러 정보와 함께 리다이렉트
            val errorMessage = URLEncoder.encode(e.message ?: "알 수 없는 오류", StandardCharsets.UTF_8)
            val errorUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .path("/auth")
                .queryParam("error", errorMessage)
                .build()
                .toUriString()
            
            redirectStrategy.sendRedirect(request, response, errorUrl)
        }
    }

    private fun clearAuthenticationAttributes(request: HttpServletRequest, response: HttpServletResponse) {
        super.clearAuthenticationAttributes(request)
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
    }
}

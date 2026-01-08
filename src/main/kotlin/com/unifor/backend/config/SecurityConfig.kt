<<<<<<< HEAD
package com.unifor.backend.config

import com.unifor.backend.security.JwtAuthenticationFilter
import com.unifor.backend.security.OAuth2AuthenticationSuccessHandler
=======
﻿package com.unifor.backend.config

import com.unifor.backend.security.JwtAuthenticationFilter
import com.unifor.backend.security.OAuth2AuthenticationFailureHandler
import com.unifor.backend.security.OAuth2AuthenticationSuccessHandler
import com.unifor.backend.security.HttpCookieOAuth2AuthorizationRequestRepository
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
<<<<<<< HEAD
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler
=======
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
    private val httpCookieOAuth2AuthorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
) {
    
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    // 공개 엔드포인트
                    .requestMatchers(
                        "/health",
                        "/actuator/**",
<<<<<<< HEAD
                        "/api/auth/signup",
                        "/api/auth/login",
                        "/oauth2/**",
                        "/login/oauth2/**"
=======
                        "/auth/signup",
                        "/auth/login",
                        "/oauth2/**",
                        "/login/oauth2/**",
                        "/assets/**",
                        "/marketplace/**"
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
                    ).permitAll()
                    // 나머지는 인증 필요
                    .anyRequest().authenticated()
            }
<<<<<<< HEAD
            .oauth2Login { oauth2 ->
                oauth2.successHandler(oAuth2AuthenticationSuccessHandler)
=======
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint { _, response, _ ->
                    response.sendError(401, "Unauthorized")
                }
            }
            .oauth2Login { oauth2 ->
                oauth2.authorizationEndpoint { it.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository) }
                oauth2.successHandler(oAuth2AuthenticationSuccessHandler)
                oauth2.failureHandler(oAuth2AuthenticationFailureHandler)
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf(
                "https://uniforge.kr",
                "http://localhost:5173",
                "http://localhost:3000"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
            maxAge = 3600L
        }
        
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}
<<<<<<< HEAD
=======



>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

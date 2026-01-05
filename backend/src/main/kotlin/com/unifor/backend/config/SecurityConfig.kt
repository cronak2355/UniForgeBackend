package com.unifor.backend.config

import com.unifor.backend.security.JwtAuthenticationFilter
import com.unifor.backend.security.OAuth2AuthenticationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
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
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler
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
                    // ê³µê°œ ?”ë“œ?¬ì¸??(?¸ì¦ ë¶ˆí•„??
                    .requestMatchers(
                        "/health",
                        "/actuator/**",
                        "/api/auth/signup",
                        "/api/auth/login",
                        "/oauth2/**",
                        "/login/oauth2/**"
                    ).permitAll()
                    // Asset ì¡°íšŒ??ê³µê°œ
                    .requestMatchers(HttpMethod.GET, "/assets", "/assets/**").permitAll()
                    // ?˜ë¨¸ì§€???¸ì¦ ?„ìš”
                    .anyRequest().authenticated()
            }
            .exceptionHandling { handling ->
                // ??ƒ 401 ë°˜í™˜, ë¦¬ë‹¤?´ë ‰???†ìŒ (API ?œë²„?´ë?ë¡?
                handling.authenticationEntryPoint { _, response, authException ->
                    response.sendError(
                        jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED,
                        authException.message ?: "Unauthorized"
                    )
                }
            }
            .oauth2Login { oauth2 ->
                oauth2.successHandler(oAuth2AuthenticationSuccessHandler)
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
                "http://localhost:3000",
                "http://localhost:5174",
                "http://localhost:5175"
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

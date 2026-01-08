package com.unifor.backend.security

import com.unifor.backend.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class UserPrincipal(
    private val user: User,
    private val attributes: Map<String, Any> = emptyMap()
) : UserDetails, OAuth2User {
    
    val id: String get() = user.id
    val email: String get() = user.email
    
    override fun getName(): String = user.id
    
    override fun getAttributes(): Map<String, Any> = attributes
    
    override fun getAuthorities(): Collection<GrantedAuthority> = 
        listOf(SimpleGrantedAuthority("ROLE_USER"))
    
    override fun getPassword(): String? = user.password
    
    override fun getUsername(): String = user.email
    
    override fun isAccountNonExpired(): Boolean = true
    
    override fun isAccountNonLocked(): Boolean = true
    
    override fun isCredentialsNonExpired(): Boolean = true
    
    override fun isEnabled(): Boolean = true
    
    companion object {
        fun create(user: User): UserPrincipal = UserPrincipal(user)
        
        fun create(user: User, attributes: Map<String, Any>): UserPrincipal = 
            UserPrincipal(user, attributes)
    }
}

package com.ralabs.security.app.security

import com.ralabs.security.app.models.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors

class UserPrincipal(
        val id: Long?,
        private val firstName: String,
        private val lastName: String,
        private val email: String,
        private val password: String,
        private val authorities: List<GrantedAuthority>
) : UserDetails {

    companion object {
        fun create(user: User): UserDetails? {
            val authorities: List<GrantedAuthority> = user.roles
                    .stream()
                    .map { SimpleGrantedAuthority(it.roleName.name) }
                    .collect(Collectors.toList())

            return UserPrincipal(
                    user.id,
                    user.firstName,
                    user.lastName,
                    user.email,
                    user.password,
                    authorities
            )
        }

    }

    override fun getUsername(): String? {
        return email;
    }

    override fun getPassword(): String {
        return password;
    }

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return authorities
    }

    override fun isEnabled(): Boolean {
        return true;
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}
package com.ralabs.security.app.security

import com.ralabs.security.app.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Component
class CustomUserDetailsService(
        val userRepository: UserRepository
) : UserDetailsService {

    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails? {
        try {
            val user = userRepository.findByEmail(username)
            return UserPrincipal.create(user)
        } catch (e: UsernameNotFoundException) {
            logger.info("User not found with username or email : $username")
        }
        return null
    }

}
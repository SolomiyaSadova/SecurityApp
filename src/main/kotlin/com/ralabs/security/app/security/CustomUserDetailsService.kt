package com.ralabs.security.app.security

import com.ralabs.security.app.exception.ResourceNotFoundException
import com.ralabs.security.app.models.User
import com.ralabs.security.app.repository.UserDAO
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
class CustomUserDetailsService(
        val userDAO: UserDAO
) : UserDetailsService {

    lateinit var userPrincipal: UserPrincipal

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails? {
        lateinit var user: User
        try {
           user = userDAO.findByUsername(username)
        } catch (e: UsernameNotFoundException) {
            logger.info("User not found with username or email : $username")
        }
        return userPrincipal.create(user)
    }

    @Transactional
    fun loadUserById(id: Long): UserDetails? {
        val user: User = userDAO!!.findById(id).orElseThrow<RuntimeException>
        { ResourceNotFoundException("User", "id", id) }
        return userPrincipal.create(user);
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    }
}
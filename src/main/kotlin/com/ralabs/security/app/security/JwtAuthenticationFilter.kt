package com.ralabs.security.app.security

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.Optional.ofNullable

@Component
class JwtAuthenticationFilter(
        private val tokenProvider: JwtTokenProvider,
        private val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    companion object {
        const val JWT_PREFIX = "Bearer"
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse,
                                  filterChain: FilterChain) {
        try {
            doAuthenticate(request)
        } catch (ex: Exception) {
            logger.error("Could not set user authentication in security context", ex)
        }
        filterChain.doFilter(request, response)
    }

    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken.startsWith(JWT_PREFIX)) {
            bearerToken.substring(JWT_PREFIX.length + " ".length, bearerToken.length)
        } else null
    }

    private fun doAuthenticate(request: HttpServletRequest): Unit {
        val userDetails = getUserDetails(request)
        ofNullable(userDetails).ifPresent {
            val authentication = getAuthentication(it)
            SecurityContextHolder.getContext().authentication = authentication
        }
    }

    private fun getUserDetails(request: HttpServletRequest): UserDetails? {
        val jwt = getJwtFromRequest(request)
        val userId = tokenProvider.getUserIdFromJWT(jwt).toLong()
        return customUserDetailsService.loadUserById(userId)
    }

    private fun getAuthentication(userDetails: UserDetails): Authentication =
            UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
}
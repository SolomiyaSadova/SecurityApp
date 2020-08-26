package com.ralabs.security.app.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.Optional.ofNullable
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val JWT_PREFIX = "Bearer"

@Component
class JwtAuthenticationFilter(
        private val tokenProvider: JwtTokenProvider,
        private val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse,
                                  filterChain: FilterChain) {
        try {
            doAuthenticate(request)
            logger.info("Authenticate user in security context")
        } catch (ex: Exception) {
            SecurityContextHolder.getContext().authentication = null
            logger.info("Could not authenticate user authentication in security context", ex)
        }
        filterChain.doFilter(request, response)
    }

    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (!bearerToken.isNullOrBlank() && bearerToken.startsWith(JWT_PREFIX)) {
            bearerToken.substring(7, bearerToken.length);
        } else null
    }

    private fun doAuthenticate(request: HttpServletRequest): Unit {
        val userDetails = getUserDetails(request)
        ofNullable(userDetails).ifPresent {
            val authentication = getAuthentication(it, request)
            SecurityContextHolder.getContext().authentication = authentication
        }
    }


    private fun getUserDetails(request: HttpServletRequest): UserDetails? {
        val jwt = getJwtFromRequest(request)
        return if (!jwt.isNullOrBlank() && tokenProvider.validateToken(jwt)) {
            val username = tokenProvider.getUserIdFromJWT(jwt)
            customUserDetailsService.loadUserByUsername(username)
        } else {
            null
        }
    }

    private fun getAuthentication(userDetails: UserDetails, request: HttpServletRequest): Authentication {
        val authentication = UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.authorities)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        return authentication
    }
}
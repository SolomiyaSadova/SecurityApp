package com.ralabs.security.app.security

import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
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
        } catch (ex: ExpiredJwtException) {
            val isRefreshToken: String = request.getHeader("isRefreshToken");
            val requestURL: String = request.requestURL.toString();
            // allow for Refresh Token creation if following conditions are true.
            if (isRefreshToken == "true" && requestURL.contains("refreshToken")) {
                allowForRefreshToken(ex, request);
            }
        } catch (ex: Exception) {
            SecurityContextHolder.getContext().authentication = null
            logger.info("Could not authenticate user authentication in security context", ex)
        }
        filterChain.doFilter(request, response)
    }


    fun getJwtFromRequest(request: HttpServletRequest): String? {
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
        return if (!jwt.isNullOrBlank() && tokenProvider.validateToken(request, jwt)) {
            val claims = tokenProvider.getUserClaims(jwt)
            val username = claims.subject
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

    private fun allowForRefreshToken(ex: ExpiredJwtException, request: HttpServletRequest) {

        // create a UsernamePasswordAuthenticationToken with null values.
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                null, null, null)
        // After setting the Authentication in the context, we specify
        // that the current user is authenticated. So it passes the
        // Spring Security Configurations successfully.
        SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
        // Set the claims so that in controller we will be using it to create
        // new JWT
        request.setAttribute("claims", ex.claims)
    }
}
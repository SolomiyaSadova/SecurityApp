package com.ralabs.security.app.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.hibernate.bytecode.BytecodeLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey
import javax.servlet.http.HttpServletRequest


@Component
class JwtTokenProvider(
        @Value("\${app.jwtSecret}")
        val jwtSecret: String,
        @Value("\${app.jwtExpirationInMs}")
        val jwtExpirationInMs: String,
        @Value("\${jwt.refreshExpirationDateInMs}")
        val refreshExpirationDateInMs: String
) {

    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs.toLong())
        val claims = Jwts.claims().setSubject(authentication.name)
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userPrincipal.username)
                .setIssuedAt(Date())
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact()
    }

    fun doGenerateRefreshToken(claims: Claims): String {
        return Jwts.builder().setClaims(claims).setSubject(claims.subject).setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + refreshExpirationDateInMs.toLong()))
                .signWith(secretKey, SignatureAlgorithm.HS512).compact()
    }

    fun getUserClaims(token: String?): Claims {
        return try {
            val claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
            claims.body
        } catch (e: ExpiredJwtException) {
            BytecodeLogger.LOGGER.error("Jwt token is expired")
            e.claims
        }
    }

    fun validateToken(request: HttpServletRequest, authToken: String?): Boolean {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(authToken)
            return true
        } catch (ex: SecurityException) {
            logger.error("Invalid JWT signature - ${ex.message}")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
        }
        return false
    }

}
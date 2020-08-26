package com.ralabs.security.app.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtTokenProvider(
        @Value("\${app.jwtSecret}")
        val jwtSecret: String,
        @Value("\${app.jwtExpirationInMs}")
        val jwtExpirationInMs: String
) {

    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs.toLong())
        return Jwts.builder()
                .setSubject(userPrincipal.username)
                .setIssuedAt(Date())
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact()
    }

    fun getUserIdFromJWT(token: String?): String {
        val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
        return claims.subject
    }

    fun validateToken(authToken: String?): Boolean {
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
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
        }
        return false
    }
}
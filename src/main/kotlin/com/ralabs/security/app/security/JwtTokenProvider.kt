package com.ralabs.security.app.security

import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.SignatureException
import java.util.*
import io.jsonwebtoken.UnsupportedJwtException


@Component
class JwtTokenProvider {

    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    @Value("\${app.jwtSecret}")
    lateinit var jwtSecret: String

    @Value("\${app.jwtExpirationInMs}")
    lateinit var jwtExpirationInMs: String

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs.toLong())
        return Jwts.builder()
                .setSubject(userPrincipal.id.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact()
    }

    fun getUserIdFromJWT(token: String?): String {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .body
                .subject
    }
}
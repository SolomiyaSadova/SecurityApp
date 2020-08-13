package com.ralabs.security.app.request

data class JwtAuthenticationResponse(
        val accessToken: String,
        val tokenType: String = "Bearer"
)
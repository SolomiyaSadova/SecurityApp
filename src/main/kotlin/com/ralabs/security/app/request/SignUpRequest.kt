package com.ralabs.security.app.request

data class LoginRequest(
        val email: String,
        val username: String,
        val password: String
)
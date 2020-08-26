package com.ralabs.security.app.request

data class UserResponse(
        val id: Long,
        val firstName: String,
        val lastName: String,
        val email: String
)
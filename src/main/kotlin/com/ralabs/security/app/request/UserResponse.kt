package com.ralabs.security.app.request

import com.ralabs.security.app.models.User


data class UserResponse(
        val id: Long,
        val email: String,
        val username: String
)
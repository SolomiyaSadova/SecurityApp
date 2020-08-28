package com.ralabs.security.app.request.password

import com.ralabs.security.app.validation.constraints.SecurePassword

data class PasswordResetRequest(
        val token: String = "",
        @get:SecurePassword
        val newPassword: String
)
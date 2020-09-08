package com.ralabs.security.app.request.password

import com.ralabs.security.app.validation.constraints.SecurePassword

data class PasswordChangeRequest(
        val oldPassword: String,
        @get:SecurePassword
        val newPassword: String,
        val newPasswordConfirm: String
)
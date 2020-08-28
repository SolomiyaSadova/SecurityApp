package com.ralabs.security.app.request

import com.ralabs.security.app.validation.constraints.ConfirmPassword
import com.ralabs.security.app.validation.constraints.SecurePassword
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

data class SignUpRequest(
        @get:NotEmpty
        val firstName: String,
        @get:NotEmpty
        val lastName: String,
        @get:NotEmpty
        @get:Email
        val email: String,
        @get:SecurePassword
        val password: String,
        @get:NotEmpty
       // @get:ConfirmPassword
        val confirmPassword: String
)
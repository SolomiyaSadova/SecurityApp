package com.ralabs.security.app.request

import javax.validation.constraints.Email

data class SignUpRequest(
        val firstName: String,
        val lastName: String,
      //  @Email
        val email: String,
        val password: String
)
package com.ralabs.security.app.service.auth

import com.ralabs.security.app.models.User
import com.ralabs.security.app.models.VerificationToken
import com.ralabs.security.app.request.LoginRequest
import com.ralabs.security.app.request.SignUpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication

interface IAuthService {
    fun encodePassword(password: String): String

    fun assignUserRole(user: User): User

    fun toUser(signUpRequest: SignUpRequest): User

    fun toUsernamePasswordAuthenticationToken(loginRequest: LoginRequest): UsernamePasswordAuthenticationToken

    fun authenticate(loginRequest: LoginRequest): Authentication

    fun isPasswordConfirmPasswordMatched(password: String, confirmPassword: String): Boolean

    fun createVerificationToken(user: User, token: String): Unit

    fun getVerificationToken(verificationToken: String): VerificationToken?

    fun saveUserAfterEmailConfirmation(token: String): Unit

    fun checkVerificationToken(token: String): String
    fun isUserVerified(authentication: Authentication): Boolean
}
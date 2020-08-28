package com.ralabs.security.app.service

import com.ralabs.security.app.models.PasswordResetToken
import com.ralabs.security.app.models.User
import com.ralabs.security.app.repository.PasswordResetTokenRepository
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.request.ApiResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*


@Service
class UserService(
        val passwordEncoder: PasswordEncoder,
        val userRepository: UserRepository,
        val passwordResetTokenRepository: PasswordResetTokenRepository
) {
    fun checkIfValidOldPassword(user: User, oldPassword: String): Boolean {
        return passwordEncoder.matches(oldPassword, user.password)
    }

    fun changeUserPassword(user: User, password: String) {
        user.password = passwordEncoder.encode(password)
        userRepository.save(user)
    }

    fun createPasswordResetTokenForUser(user: User, token: String) {
        val myToken = PasswordResetToken.build(token, user)
        passwordResetTokenRepository.save(myToken)
    }

    fun validatePasswordResetToken(token: String): ApiResponse {
        val passToken: PasswordResetToken = passwordResetTokenRepository.findByToken(token)
        return if (!isTokenFound(passToken)) ApiResponse(false, "Token is invalid")
        else if (isTokenExpired(passToken)) ApiResponse(false, "Token is expired")
        else ApiResponse(true, "All is good")
    }

    fun getUserByPasswordResetToken(token: String): User {
        val passwordResetToken = passwordResetTokenRepository.findByToken(token)
        return passwordResetToken.user
    }

    private fun isTokenFound(passToken: PasswordResetToken?): Boolean {
        return passToken != null
    }

    private fun isTokenExpired(passToken: PasswordResetToken): Boolean {
        val cal = Calendar.getInstance()
        return passToken.expiryDate.before(cal.time)
    }

}
package com.ralabs.security.app.controller

import com.ralabs.security.app.exception.ConfirmPasswordDoesntMatchPasswordException
import com.ralabs.security.app.repository.PasswordResetTokenRepository
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.request.ApiResponse
import com.ralabs.security.app.request.password.PasswordChangeRequest
import com.ralabs.security.app.request.password.PasswordResetRequest
import com.ralabs.security.app.security.JwtAuthenticationFilter
import com.ralabs.security.app.security.JwtTokenProvider
import com.ralabs.security.app.service.AuthService
import com.ralabs.security.app.service.UserService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


@Validated
@RestController
class UserController(
        val userService: UserService,
        val authService: AuthService,
        val userRepository: UserRepository,
        val passwordResetTokenRepository: PasswordResetTokenRepository,
        val jwtTokenProvider: JwtTokenProvider,
        val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @PostMapping("/changePassword")
    fun changePassword(@Valid @RequestBody passwordChangeRequest: PasswordChangeRequest)
            : ResponseEntity<ApiResponse> {
        val user = userRepository.findByEmail(SecurityContextHolder.getContext().authentication.name)
        if (!userService.checkIfValidOldPassword(user, passwordChangeRequest.oldPassword)) {
            return ResponseEntity(ApiResponse(false, "Old password is invalid"),
                    HttpStatus.BAD_REQUEST)
        }
        if (passwordChangeRequest.oldPassword == passwordChangeRequest.newPassword) {
            return ResponseEntity(ApiResponse(false, "New password is the same as old"),
                    HttpStatus.BAD_REQUEST)
        }
        if (!authService.isPasswordConfirmPasswordMatched(
                        passwordChangeRequest.newPassword, passwordChangeRequest.newPasswordConfirm)) {
            throw ConfirmPasswordDoesntMatchPasswordException("Confirm password field doesn't match the password field")
        }
        userService.changeUserPassword(user, passwordChangeRequest.newPassword)

        return ResponseEntity(ApiResponse(true,
                "Success. New password - ${passwordChangeRequest.newPassword}"),
                HttpStatus.OK)
    }

    @PostMapping("/resetPassword")
    fun resetPassword(request: HttpServletRequest,
                      @RequestParam("email") email: String): ResponseEntity<ApiResponse> {
        val user = userRepository.findByEmail(email)
        val token = UUID.randomUUID().toString()
        userService.createPasswordResetTokenForUser(user, token)
//        mailSender.send(constructResetTokenEmail(getAppUrl(request),
//                request.locale, token, user))
        return ResponseEntity(ApiResponse(true, "Success"), HttpStatus.OK)
    }

    @PostMapping("/resetPasswordWithToken")
    fun resetPasswordWithToken(request: HttpServletRequest, @RequestBody passwordResetRequest: PasswordResetRequest
    ): ResponseEntity<ApiResponse> {
        val result = userService.validatePasswordResetToken(passwordResetRequest.token)

        return if (!result.success) {
            ResponseEntity(result, HttpStatus.BAD_REQUEST)
        } else {
            val user = userService.getUserByPasswordResetToken(passwordResetRequest.token)
            userService.changeUserPassword(user, passwordResetRequest.newPassword);
            ResponseEntity(ApiResponse(true, "Success"), HttpStatus.OK)
        }
    }


    @GetMapping("/refreshToken")
    fun refreshToken(request: HttpServletRequest): ResponseEntity<*>? {
        val token = jwtAuthenticationFilter.getJwtFromRequest(request)
        val claims = jwtTokenProvider.getUserClaims(token)
        val newToken: String? = jwtTokenProvider.doGenerateRefreshToken(claims)
        return ResponseEntity(ApiResponse(true, "Token is refreshed. Token - $newToken"),
                HttpStatus.OK)
    }

}
package com.ralabs.security.app.controller

import com.ralabs.security.app.repository.PasswordResetTokenRepository
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.request.ApiResponse
import com.ralabs.security.app.request.password.PasswordChangeRequest
import com.ralabs.security.app.request.password.PasswordResetRequest
import com.ralabs.security.app.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


@Validated
@RestController
class UserController(
        val userService: UserService,
        val userRepository: UserRepository,
        val passwordResetTokenRepository: PasswordResetTokenRepository
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
        if (passwordChangeRequest.newPassword != passwordChangeRequest.newPasswordConfirm) {
            return ResponseEntity(ApiResponse(false, "Confirm password field doesn't match the password"),
                    HttpStatus.BAD_REQUEST)
        }
        userService.changeUserPassword(user, passwordChangeRequest.newPassword)

        return ResponseEntity(ApiResponse(true, "Success. New password - ${passwordChangeRequest.newPassword}"),
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
}
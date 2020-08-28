package com.ralabs.security.app.validation.constraints

import com.ralabs.security.app.request.SignUpRequest
import com.ralabs.security.app.service.AuthService
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext


internal class ConfirmPasswordValidator(
        private val authService: AuthService
) : ConstraintValidator<ConfirmPassword, SignUpRequest> {
    override fun isValid(signUpRequest: SignUpRequest, context: ConstraintValidatorContext): Boolean {
        return authService.isPasswordConfirmPasswordMatched(signUpRequest.password, signUpRequest.confirmPassword)
    }
}

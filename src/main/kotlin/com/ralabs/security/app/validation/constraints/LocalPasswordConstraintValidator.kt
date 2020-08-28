package com.ralabs.security.app.validation.constraints

import org.springframework.context.annotation.Profile
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

@Profile("local")
class LocalPasswordConstraintValidator : PasswordConstraintValidator() {
    override fun initialize(arg0: SecurePassword) {}
    override fun isValid(password: String?, context: ConstraintValidatorContext): Boolean = true
}
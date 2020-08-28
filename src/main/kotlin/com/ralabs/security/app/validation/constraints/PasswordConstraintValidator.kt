package com.ralabs.security.app.validation.constraints

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

abstract class PasswordConstraintValidator : ConstraintValidator<SecurePassword, String>
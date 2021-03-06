package com.ralabs.security.app.validation.constraints

import javax.validation.ConstraintValidator

abstract class PasswordConstraintValidator : ConstraintValidator<SecurePassword, String>
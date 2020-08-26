package com.ralabs.security.app.service

import com.ralabs.security.app.request.SignUpRequest
import org.springframework.stereotype.Component
import javax.validation.ConstraintViolationException
import javax.validation.Validation
import javax.validation.ValidatorFactory


@Component
class UserAuthValidator {
    fun validateInput(input: SignUpRequest) {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        val validator = factory.validator
        val violations = validator.validate(input)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }
}
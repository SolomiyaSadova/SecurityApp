package com.ralabs.security.app.validation.constraints

import org.passay.*
import org.springframework.context.annotation.Profile
import java.util.stream.Collectors
import javax.validation.ConstraintValidatorContext

@Profile("!local")
internal class SecurePasswordConstraintValidator : PasswordConstraintValidator() {
    override fun initialize(arg0: SecurePassword) {}
    override fun isValid(password: String?, context: ConstraintValidatorContext): Boolean {
        val validator = PasswordValidator(listOf(
                LengthRule(8, 30),
                UppercaseCharacterRule(1),
                DigitCharacterRule(1),
                // SpecialCharacterRule(1),
                NumericalSequenceRule(3, false),
                AlphabeticalSequenceRule(3, false),
                QwertySequenceRule(3, false),
                WhitespaceRule()))
        val result = validator.validate(PasswordData(password))
        if (result.isValid) {
            return true
        }
        val messages = validator.getMessages(result)
        val messageTemplate = messages.stream().collect(Collectors.joining(","))
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation()
        return false
    }
}
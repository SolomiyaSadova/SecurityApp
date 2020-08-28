package com.ralabs.security.app.validation.constraints

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [SecurePasswordConstraintValidator::class])
@Target(allowedTargets = [
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
])
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfirmPassword(
        val message: String = "Password and Confirm Password do not match.",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)
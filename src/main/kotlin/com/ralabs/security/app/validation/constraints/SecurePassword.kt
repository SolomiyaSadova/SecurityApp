package com.ralabs.security.app.validation.constraints

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass


@MustBeDocumented
@Constraint(validatedBy = [PasswordConstraintValidator::class])
@Target(allowedTargets = [
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
])
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidPassword(
        val message: String = "Invalid Password",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)
package com.ralabs.security.app.exception

class ResourceNotFoundException(
        private val resourceName: String?,
        private val fieldName: String?,
        private val fieldValue: Any?
) : RuntimeException(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue))
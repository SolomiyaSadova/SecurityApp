package com.ralabs.security.app.exception

class UserAlreadyExistsException(override val message: String?) : Exception(message)
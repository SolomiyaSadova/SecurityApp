package com.ralabs.security.app.exception

import com.ralabs.security.app.request.ApiResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ControllerAdviceRequestError : ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [(UserAlreadyExistsException::class)])
    fun handleUserAlreadyExists(ex: UserAlreadyExistsException, request: WebRequest): ResponseEntity<ApiResponse> {
        return ResponseEntity(ApiResponse(false, ex.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException, request: WebRequest): ResponseEntity<ApiResponse> {
        return ResponseEntity(ApiResponse(false, ex.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ConfirmPasswordDoesntMatchPasswordException::class)
    fun handleConfirmPasswordDoesntMatchPasswordException(ex: ConfirmPasswordDoesntMatchPasswordException,
                                                            request: WebRequest): ResponseEntity<ApiResponse> {
        return ResponseEntity(ApiResponse(false, ex.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
            ex: Exception?, request: WebRequest?): ResponseEntity<ApiResponse>? {
        return ResponseEntity(ApiResponse(false,
                "Access denied message here"), HttpStatus.FORBIDDEN)
    }
}
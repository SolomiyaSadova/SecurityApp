package com.ralabs.security.app.controller

import com.ralabs.security.app.event.OnRegistrationCompleteEvent
import com.ralabs.security.app.exception.ConfirmPasswordDoesntMatchPasswordException
import com.ralabs.security.app.exception.UserAlreadyExistsException
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.request.ApiResponse
import com.ralabs.security.app.request.JwtAuthenticationResponse
import com.ralabs.security.app.request.LoginRequest
import com.ralabs.security.app.request.SignUpRequest
import com.ralabs.security.app.security.JwtTokenProvider
import com.ralabs.security.app.service.auth.AuthService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest
import javax.validation.Valid

@RestController
@RequestMapping("/auth")
class AuthController(
        val authService: AuthService,
        val tokenProvider: JwtTokenProvider,
        val eventPublisher: ApplicationEventPublisher,
        val userRepository: UserRepository
) {

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest, request: WebRequest)
            : ResponseEntity<*> {

        if (userRepository.existsByEmail(signUpRequest.email)) {
            throw UserAlreadyExistsException("User with email ${signUpRequest.email} already exists")
        }
        if (!authService.isPasswordConfirmPasswordMatched(signUpRequest.password, signUpRequest.confirmPassword)) {
            throw ConfirmPasswordDoesntMatchPasswordException("Confirm password field doesn't match the password field")
        }
        val userWithRole = authService.assignUserRole(authService.toUser(signUpRequest))
        val savedUser = authService.saveUser(userWithRole)

        eventPublisher.publishEvent(OnRegistrationCompleteEvent(userWithRole,
//                request.locale, request.contextPath,
                "confirm registration"))

        return ResponseEntity.ok(savedUser.toResponse())
    }


    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest)
            : ResponseEntity<*> {
        val authentication = authService.authenticate(loginRequest)
        val verified = authService.isUserVerified(authentication);
        if(!verified) {
            return ResponseEntity(ApiResponse(false, "You didn't confirm registration. Check your email!"),
            HttpStatus.BAD_REQUEST)
        }
        val jwt = tokenProvider.generateToken(authentication)
        return ResponseEntity.ok(JwtAuthenticationResponse(jwt))
    }

    @GetMapping("/signup/confirm")
    fun confirmRegistrationWithLink(@RequestParam token: String): ResponseEntity<ApiResponse> {
        val answer = authService.checkVerificationToken(token)
        if (answer != "Success") {
            return ResponseEntity(ApiResponse(false, answer), HttpStatus.BAD_REQUEST)
        }
        authService.saveUserAfterEmailConfirmation(token);
        return ResponseEntity(ApiResponse(true, "You was successfully registered"), HttpStatus.OK)
    }
}
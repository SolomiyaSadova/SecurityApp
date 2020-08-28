package com.ralabs.security.app.controller

import com.ralabs.security.app.models.Role
import com.ralabs.security.app.models.RoleName
import com.ralabs.security.app.models.User
import com.ralabs.security.app.repository.RoleRepository
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.request.ApiResponse
import com.ralabs.security.app.request.JwtAuthenticationResponse
import com.ralabs.security.app.request.LoginRequest
import com.ralabs.security.app.request.SignUpRequest
import com.ralabs.security.app.security.JwtTokenProvider
import com.ralabs.security.app.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/auth")
class AuthController(
        val authService: AuthService,
        val userRepository: UserRepository,
        val tokenProvider: JwtTokenProvider
) {

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest)
            : ResponseEntity<*> {

        if (userRepository.existsByEmail(signUpRequest.email)) {
            return ResponseEntity(ApiResponse(false, "Email has been used by another account"),
                    HttpStatus.BAD_REQUEST)
        }
        if (!authService.isPasswordConfirmPasswordMatched(signUpRequest.password, signUpRequest.confirmPassword)) {
            return ResponseEntity(ApiResponse(false, "Confirm password field doesn't match the password"),
                    HttpStatus.BAD_REQUEST)
        }
        val userWithRole = authService.assignUserRole(authService.toUser(signUpRequest))
        val savedUser = userRepository.save(userWithRole)

        return ResponseEntity.ok(savedUser.toResponse())
    }


    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest)
            : ResponseEntity<JwtAuthenticationResponse> {
        val authentication = authService.authenticate(loginRequest)
        val jwt = tokenProvider.generateToken(authentication)

        return ResponseEntity.ok(JwtAuthenticationResponse(jwt))
    }

}
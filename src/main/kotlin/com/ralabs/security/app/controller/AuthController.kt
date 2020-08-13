package com.ralabs.security.app.controller

import com.ralabs.security.app.exception.AppException
import com.ralabs.security.app.models.Role
import com.ralabs.security.app.models.RoleName
import com.ralabs.security.app.models.User
import com.ralabs.security.app.repository.RoleDAO
import com.ralabs.security.app.repository.UserDAO
import com.ralabs.security.app.request.ApiResponse
import com.ralabs.security.app.request.JwtAuthenticationResponse
import com.ralabs.security.app.request.LoginRequest
import com.ralabs.security.app.security.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping("/auth")
class AuthController(
        val userDAO: UserDAO,
        val passwordEncoder: PasswordEncoder,
        val roleDAO: RoleDAO,
        val authenticationManager: AuthenticationManager,
        val tokenProvider: JwtTokenProvider
) {
    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signUpRequest: LoginRequest): ApiResponse {
        if (userDAO.existsByUsername(signUpRequest.username)!!) {
            return ApiResponse(false, "Username is already taken!")
        }
        var user: User = User(signUpRequest.username, signUpRequest.password);
        user.password = passwordEncoder.encode(signUpRequest.password);
                try {
                    val userRole: Role = roleDAO.findByRoleName(RoleName.ROLE_USER)
                    user.role = Collections.singleton(userRole)
                 //   userDAO.save(user)
                } catch (e: AppException) {
                     AppException("User Role not set.")
                }
        userDAO.save(user)
        return ApiResponse(true, "User registered successfully")
    }


    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<*>? {
        val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.username,
                        loginRequest.password
                )
        )
        SecurityContextHolder.getContext().authentication = authentication
        val jwt: String = tokenProvider.generateToken(authentication)
        return ResponseEntity.ok<Any>(JwtAuthenticationResponse(jwt))
    }
}
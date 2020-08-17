package com.ralabs.security.app.controller

import com.ralabs.security.app.models.Role
import com.ralabs.security.app.models.RoleName
import com.ralabs.security.app.models.User
import com.ralabs.security.app.repository.RoleRepository
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.request.JwtAuthenticationResponse
import com.ralabs.security.app.request.LoginRequest
import com.ralabs.security.app.request.UserResponse
import com.ralabs.security.app.security.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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
        val userRepository: UserRepository,
        val passwordEncoder: PasswordEncoder,
        val roleRepository: RoleRepository,
        val authenticationManager: AuthenticationManager,
        val tokenProvider: JwtTokenProvider
) {
    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signUpRequest: LoginRequest): ResponseEntity<UserResponse> {
        if (userRepository.existsByUsername(signUpRequest.username)!!) {
            return ResponseEntity.badRequest().build()
        }
        val userWithRole = assignUserRole(toUser(signUpRequest))
        val savedUser = userRepository.save(userWithRole)
        return ResponseEntity.ok(savedUser.toResponse())
    }


    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest):
            ResponseEntity<JwtAuthenticationResponse> {
        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.username,
                        loginRequest.password
                )
        )
        val context = SecurityContextHolder.getContext()
        context.authentication = authentication
        val jwt = tokenProvider.generateToken(authentication)
        return ResponseEntity.ok(JwtAuthenticationResponse(jwt))
    }

    private fun encodePassword(password: String): String = passwordEncoder.encode(password)

    private fun assignUserRole(user: User): User {
        val userRole: Role = roleRepository.findByRoleName(RoleName.ROLE_USER)
        return user.copy(role = Collections.singleton(userRole))
    }

    private fun toUser(signUpRequest: LoginRequest): User = User(
            email = signUpRequest.email,
            username = signUpRequest.username,
            password = encodePassword(signUpRequest.password)
    )
}
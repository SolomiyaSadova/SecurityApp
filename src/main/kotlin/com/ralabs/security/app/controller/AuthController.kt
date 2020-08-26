package com.ralabs.security.app.controller

import com.ralabs.security.app.models.Role
import com.ralabs.security.app.models.RoleName
import com.ralabs.security.app.models.User
import com.ralabs.security.app.repository.RoleRepository
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.request.*
import com.ralabs.security.app.security.JwtTokenProvider
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.regex.Pattern
import javax.validation.Valid

const val REGEX = "^(.+)@(.+)$";

@RestController
@RequestMapping("/auth")
class AuthController(
        val authenticationManager: AuthenticationManager,
        val userRepository: UserRepository,
        val passwordEncoder: PasswordEncoder,
        val roleRepository: RoleRepository,
        val tokenProvider: JwtTokenProvider
) {

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest)
            : ResponseEntity<*> {

        if (userRepository.existsByEmail(signUpRequest.email)) {
            return ResponseEntity(ApiResponse(false, "Email has been used by another account"),
                    HttpStatus.BAD_REQUEST)
        }
        if(!validateEmail(signUpRequest.email)) {
            return ResponseEntity(ApiResponse(false, "Email is invalid"),
                    HttpStatus.BAD_REQUEST)
        }
        val userWithRole = assignUserRole(toUser(signUpRequest))
        val savedUser = userRepository.save(userWithRole)
        return ResponseEntity.ok(savedUser.toResponse())
    }


    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest)
            : ResponseEntity<JwtAuthenticationResponse> {
        val authentication = authenticate(loginRequest)
        val jwt = tokenProvider.generateToken(authentication)
        return ResponseEntity.ok(JwtAuthenticationResponse(jwt))
    }

    private fun encodePassword(password: String): String = passwordEncoder.encode(password)

    private fun assignUserRole(user: User): User {
        val userRole: Role = roleRepository.findByRoleName(RoleName.ROLE_USER)
        return user.copy(role = Collections.singleton(userRole))
    }

    private fun toUser(signUpRequest: SignUpRequest) = User(
            firstName = signUpRequest.firstName,
            lastName = signUpRequest.lastName,
            email = signUpRequest.email,
            password = encodePassword(signUpRequest.password)
    )

    private fun toUsernamePasswordAuthenticationToken(loginRequest: LoginRequest)
            : UsernamePasswordAuthenticationToken =
                UsernamePasswordAuthenticationToken(
                    loginRequest.email,
                    loginRequest.password
                )

    private fun authenticate(loginRequest: LoginRequest): Authentication {
        val usernamePassword = toUsernamePasswordAuthenticationToken(loginRequest)
        val authentication = authenticationManager.authenticate(usernamePassword)
        SecurityContextHolder.getContext().authentication = authentication
        return authentication
    }

    private fun validateEmail(email: String): Boolean {
        val pattern = Pattern.compile(REGEX);
        val matcher = pattern.matcher(email);
        if(!matcher.matches()) {
            return false
        }
        return true
    }
}
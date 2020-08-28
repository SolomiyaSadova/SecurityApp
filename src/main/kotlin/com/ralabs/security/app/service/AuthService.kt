package com.ralabs.security.app.service

import com.ralabs.security.app.models.Role
import com.ralabs.security.app.models.RoleName
import com.ralabs.security.app.models.User
import com.ralabs.security.app.repository.RoleRepository
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.request.LoginRequest
import com.ralabs.security.app.request.SignUpRequest
import com.ralabs.security.app.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import java.util.regex.Pattern

@Service
class AuthService(
        val authenticationManager: AuthenticationManager,
        val passwordEncoder: PasswordEncoder,
        val roleRepository: RoleRepository
) {

    fun encodePassword(password: String): String = passwordEncoder.encode(password)

    fun assignUserRole(user: User): User {
        val userRole: Role = roleRepository.findByRoleName(RoleName.ROLE_USER)
        return user.copy(role = Collections.singleton(userRole))
    }

    fun toUser(signUpRequest: SignUpRequest) = User(
            firstName = signUpRequest.firstName,
            lastName = signUpRequest.lastName,
            email = signUpRequest.email,
            password = encodePassword(signUpRequest.password)
    )

    fun toUsernamePasswordAuthenticationToken(loginRequest: LoginRequest)
            : UsernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(
                    loginRequest.email,
                    loginRequest.password
            )

    fun authenticate(loginRequest: LoginRequest): Authentication {
        val usernamePassword = toUsernamePasswordAuthenticationToken(loginRequest)
        val authentication = authenticationManager.authenticate(usernamePassword)
        SecurityContextHolder.getContext().authentication = authentication
        return authentication
    }

    fun isPasswordConfirmPasswordMatched(password: String, confirmPassword: String): Boolean {
        if(password == confirmPassword) {
            return true
        }
        return false
    }
}

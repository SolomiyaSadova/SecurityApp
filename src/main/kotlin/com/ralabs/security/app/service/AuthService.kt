package com.ralabs.security.app.service

import com.ralabs.security.app.models.Role
import com.ralabs.security.app.models.RoleName
import com.ralabs.security.app.models.User
import com.ralabs.security.app.models.VerificationToken
import com.ralabs.security.app.repository.RoleRepository
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.repository.VerificationTokenRepository
import com.ralabs.security.app.request.LoginRequest
import com.ralabs.security.app.request.SignUpRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Transactional
@Service
class AuthService(
        val authenticationManager: AuthenticationManager,
        val userRepository: UserRepository,
        val passwordEncoder: PasswordEncoder,
        val roleRepository: RoleRepository,
        val verificationTokenRepository: VerificationTokenRepository
) : IAuthService {

    override fun encodePassword(password: String): String = passwordEncoder.encode(password)

    override fun assignUserRole(user: User): User {
        val userRole: Role = roleRepository.findByRoleName(RoleName.ROLE_USER)
        return user.copy(role = Collections.singleton(userRole))
    }

    override fun toUser(signUpRequest: SignUpRequest) = User(
            firstName = signUpRequest.firstName,
            lastName = signUpRequest.lastName,
            email = signUpRequest.email,
            password = encodePassword(signUpRequest.password)
    )

    override fun toUsernamePasswordAuthenticationToken(loginRequest: LoginRequest)
            : UsernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(
                    loginRequest.email,
                    loginRequest.password
            )

    override fun authenticate(loginRequest: LoginRequest): Authentication {
        val usernamePassword = toUsernamePasswordAuthenticationToken(loginRequest)
        val authentication = authenticationManager.authenticate(usernamePassword)
        SecurityContextHolder.getContext().authentication = authentication
        return authentication
    }

    override fun isPasswordConfirmPasswordMatched(password: String, confirmPassword: String): Boolean {
        if (password == confirmPassword) {
            return true
        }
        return false
    }

    @Transactional
    override fun createVerificationToken(user: User, token: String): Unit {
        verificationTokenRepository.save(VerificationToken.build(user, token))
    }

    override fun saveUserAfterEmailConfirmation(token: String): Unit {
        val verificationToken = getVerificationToken(token)
        val user = verificationToken?.user
        user?.verified = true
        if (user != null) {
            saveUser(user)
        }
    }

    override fun checkVerificationToken(token: String): String {
        val verificationToken = getVerificationToken(token) ?: return "Token is bad"
        val cal = Calendar.getInstance()
        if ((verificationToken.expiryDate.time.minus(cal.time.time)) <= 0) {
            return "Token is expired"
        }
        return "Success"
    }

    override fun getVerificationToken(verificationToken: String): VerificationToken? {
        return verificationTokenRepository.findByToken(verificationToken)
    }

    fun saveUser(user: User): User {
        return userRepository.save(user)
    }
}

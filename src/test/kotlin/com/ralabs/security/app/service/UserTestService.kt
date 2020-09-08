package com.ralabs.security.app.service

import com.ralabs.security.app.models.User
import com.ralabs.security.app.models.VerificationToken
import com.ralabs.security.app.repository.UserRepository
import com.ralabs.security.app.repository.VerificationTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var verificationTokenRepository: VerificationTokenRepository

    fun getUserByVerificationToken(token: String): User? {
        val verificationToken = verificationTokenRepository.findByToken(token)
        if (verificationToken != null) {
            return verificationToken.user
        }
        return null
    }
}
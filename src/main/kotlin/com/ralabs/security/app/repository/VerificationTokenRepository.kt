package com.ralabs.security.app.repository

import com.ralabs.security.app.models.User
import com.ralabs.security.app.models.VerificationToken

import org.springframework.data.jpa.repository.JpaRepository


interface VerificationTokenRepository : JpaRepository<VerificationToken?, Long?> {
    fun findByToken(token: String): VerificationToken
    fun findByUser(user: User): VerificationToken?
}
package com.ralabs.security.app.repository

import com.ralabs.security.app.models.PasswordResetToken
import com.ralabs.security.app.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.stream.Stream

@Repository
interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, Long> {

    fun findByToken(token: String): PasswordResetToken?

    @Transactional
    @Modifying
    @Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
    fun deleteAllExpiredSince(now: Date)
}
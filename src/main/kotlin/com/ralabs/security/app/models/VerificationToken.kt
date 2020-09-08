package com.ralabs.security.app.models

import java.sql.Timestamp
import java.util.*
import javax.persistence.*


@Entity
class VerificationToken(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private val id: Long? = null,
        private val token: String,

        @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
        @JoinColumn(nullable = false, name = "user_id") val user: User,
        val expiryDate: Date
) {
    companion object {
        fun build(user: User, token: String): VerificationToken = VerificationToken(
                token = token,
                user = user,
                expiryDate = calculateExpiryDate()
        )

        private const val EXPIRATION = 60 * 24
        private fun calculateExpiryDate(): Date {
            val cal = Calendar.getInstance()
            cal.time = Timestamp(cal.time.time)
            cal.add(Calendar.MINUTE, EXPIRATION)
            return Date(cal.time.time)
        }
    }
}
package com.ralabs.security.app.models

import java.util.*
import javax.persistence.*


@Entity
class PasswordResetToken(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private val id: Long? = null,
        val token: String,

        @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
        @JoinColumn(nullable = false, name = "user_id") val user: User,
        val expiryDate: Date
) {

    companion object {
        fun build(token: String, user: User): PasswordResetToken = PasswordResetToken(
                token = token,
                user = user,
                expiryDate = addMinutesToDate()
        )

        private fun addMinutesToDate(): Date {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.MINUTE, EXPIRATION)
            return calendar.time
        }

        private const val EXPIRATION = 60
    }
}
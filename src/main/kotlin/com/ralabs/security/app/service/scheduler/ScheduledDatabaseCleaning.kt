package com.ralabs.security.app.service

import com.ralabs.security.app.repository.PasswordResetTokenRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*


@Service
class ScheduledDatabaseCleaning(
        val passwordResetTokenRepository: PasswordResetTokenRepository
) {

    private val log: Logger = LoggerFactory.getLogger(ScheduledDatabaseCleaning::class.java)

    @Scheduled(fixedRateString = "\${delay.before.database.cleaning}")
    fun cleanPasswordResetTokenInDatabase(): Unit {
        log.info("Scheduled cleaning PasswordResetToken table in database...")
        passwordResetTokenRepository.deleteAllExpiredSince(getDateInRightFormat())
    }

    private fun getDateInRightFormat(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.MINUTE, 0)
        return calendar.time
    }
}
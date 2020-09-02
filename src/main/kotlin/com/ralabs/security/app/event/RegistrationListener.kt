package com.ralabs.security.app.event

import com.ralabs.security.app.models.Mail
import com.ralabs.security.app.models.User
import com.ralabs.security.app.sender.EmailService
import com.ralabs.security.app.service.AuthService
import com.ralabs.security.app.service.UserService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ThreadLocalRandom


@Component
class RegistrationListener(
        val authService: AuthService,
        val userService: UserService,
        @Qualifier("messageSource") val messages: MessageSource,
        val emailService: EmailService
) : ApplicationListener<OnRegistrationCompleteEvent> {
    @Value("\${server.port}")
    lateinit var port: String

    override fun onApplicationEvent(event: OnRegistrationCompleteEvent) {
      //  this.confirmRegistrationWithLink(event);
        this.confirmRegistrationWithCode(event);
    }

    private fun confirmRegistrationWithCode(event: OnRegistrationCompleteEvent): Unit {
        val user: User = event.user
        val token = rand(1000000000, 9999999999).toString()
        var email = Mail("", "", "")
        if(event.actionName == "confirm registration") {
            authService.createVerificationToken(user, token)
             email = Mail(user.email, "Registration Confirmation", "Your token - $token")
        }
        if(event.actionName == "reset password") {
            userService.createPasswordResetTokenForUser(user, token)
             email = Mail(user.email, "Registration Confirmation", "Your token - $token")
        }
      //  val email = Mail(user.email, "Registration Confirmation", "Your token - $token")
        emailService.sendConfirmationEmail(email)
    }

    private fun confirmRegistrationWithLink(event: OnRegistrationCompleteEvent) {
        val user: User = event.user
        val token = UUID.randomUUID().toString()
        authService.createVerificationToken(user, token)
        val confirmationUrl = event.appUrl + "/auth/signup/confirm?token=" + token
//        val message: String = messages.getMessage("message.regSucc", null, event.locale)
        val email = Mail(user.email, "Registration Confirmation", "http://localhost:${port}$confirmationUrl")
        emailService.sendConfirmationEmail(email)
    }

    private fun rand(from: Long, to: Long) : Long {
        return ThreadLocalRandom.current().nextLong(from, to)
    }
}
package com.ralabs.security.app.controller

import com.ralabs.security.app.models.Mail
import com.ralabs.security.app.sender.EmailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/mail")
class EmailSenderController(
        private val emailService: EmailService
) {

    @GetMapping("/send")
    fun sendmail(): String? {
        emailService.sendWelcomeEmail(Mail("ssolimia@gmail.com", "sadsolomia@gmail.com", "Test Subject", "Test mail"))
        return "Send"
    }
}
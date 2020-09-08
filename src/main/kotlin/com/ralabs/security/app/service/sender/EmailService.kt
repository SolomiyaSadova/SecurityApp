package com.ralabs.security.app.service.sender

import com.ralabs.security.app.models.Mail
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
import java.nio.charset.StandardCharsets
import java.util.*
import javax.mail.internet.MimeMessage


@Service
open class EmailService(
        val javaMailSender: JavaMailSender
) {

    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    @Value("\${spring.mail.username}")
    lateinit var emailFrom: String

   open fun sendConfirmationEmail(mail: Mail) {
        val message: MimeMessage = javaMailSender.createMimeMessage()
        try {
            val helper = MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name())
            helper.setTo(mail.to)
            helper.setSubject(mail.subject)
            helper.setText(mail.content)
            helper.setFrom(emailFrom)
            javaMailSender.send(message)
            logger.info("Confirmation email sent")
        } catch (e: Exception) {
            logger.info("Sending welcome email failed, check log...")
            e.printStackTrace()

        }
    }


}
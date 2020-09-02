package com.ralabs.security.app.sender

import com.ralabs.security.app.models.Mail
import com.ralabs.security.app.security.JwtTokenProvider
import freemarker.template.Configuration
import freemarker.template.Template
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
import java.nio.charset.StandardCharsets
import java.util.*
import javax.mail.internet.MimeMessage


@Service
class EmailService(
        val javaMailSender: JavaMailSender,
        val freeMarkerConfigurer: FreeMarkerConfigurer,
        val config: Configuration
) {

    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    @Value("\${spring.mail.username}")
    lateinit var emailFrom: String

    fun sendConfirmationEmail(mail: Mail) {
        val message: MimeMessage = javaMailSender.createMimeMessage()
        val model: MutableMap<String, Any> = HashMap()
        model["Name"] = mail.to
        model["location"] = "Lviv,Ukraine"
        try {
            val t: Template = config.getTemplate("confirm.ftl")
            val helper = MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name())
            val html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model)

            helper.setTo(mail.to)
            helper.setText(html, true)
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
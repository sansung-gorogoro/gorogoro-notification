package com.gorogoro.notification.adapter.out.mail

import com.gorogoro.notification.application.port.out.SendEmailPort
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class SmtpMailAdapter(
    private val javaMailSender: JavaMailSender
) : SendEmailPort {

    override fun sendEmail(to: String, subject: String, body: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.subject = subject
        message.text = body
        javaMailSender.send(message)
    }
}

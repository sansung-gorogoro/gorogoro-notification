package com.gorogoro.notification.application.service

import com.gorogoro.notification.application.port.`in`.SendNotificationUseCase
import com.gorogoro.notification.application.port.out.SendEmailPort
import com.gorogoro.notification.domain.model.EmailType
import com.gorogoro.notification.domain.model.NotificationCommand
import org.springframework.stereotype.Service
import java.util.concurrent.ThreadLocalRandom

@Service
class NotificationService(
    private val sendEmailPort: SendEmailPort
) : SendNotificationUseCase {

    override fun sendNotification(command: NotificationCommand): String? {
        return when (command.type) {
            EmailType.WELCOME -> {
                sendWelcomeEmail(command.email)
                null
            }
            EmailType.EMAIL_VERIFICATION -> {
                val verificationCode = generateVerificationCode()
                sendVerificationEmail(command.email, verificationCode)
                verificationCode
            }
            EmailType.PAYMENT_CONFIRMATION -> {
                sendPaymentConfirmationEmail(command.email, command.payload)
                null
            }
        }
    }

    private fun sendWelcomeEmail(email: String) {
        val subject = "Gorogoro에 오신것을 환영합니다!"
        val body = "Thank you for joining us. We are excited to have you on board."
        sendEmailPort.sendEmail(email, subject, body)
    }

    private fun sendVerificationEmail(email: String, code: String) {
        val subject = "[Gorogoro] Email Verification"
        val body = "Your verification code is: $code\nPlease enter this code to verify your email address."
        sendEmailPort.sendEmail(email, subject, body)
    }

    private fun sendPaymentConfirmationEmail(email: String, payload: Map<String, Any>) {
        val amount = payload["amount"] ?: "Unknown"
        val orderId = payload["orderId"] ?: "Unknown"
        val subject = "Payment Confirmation - Order #$orderId"
        val body = "Your payment of $amount has been successfully processed. Thank you for your purchase."
        sendEmailPort.sendEmail(email, subject, body)
    }

    private fun generateVerificationCode(): String {
        return ThreadLocalRandom.current().nextInt(100000, 1000000).toString()
    }
}

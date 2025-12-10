package com.gorogoro.notification.adapter.out.mail

import com.gorogoro.notification.application.port.out.SendEmailPort
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * 실제 메일 발송 테스트
 *
 * 실행 전 IntelliJ Run Configuration의 Environment variables에 다음 값들이 설정되어 있어야 합니다:
 * - MAIL_USERNAME: 발송자 구글 이메일 (예: test@gmail.com)
 * - MAIL_PASSWORD: 발송자 앱 비밀번호 (16자리)
 * - RABBIT_HOST: (Optional) RabbitMQ 호스트, 없으면 연결 에러 로그가 발생할 수 있으나 메일 발송은 동작할 수 있음.
 * - RABBIT_ID: (Optional)
 * - RABBIT_PASSWORD: (Optional)
 *
 * 또한 수신자 이메일 확인을 위해 코드 내 `to` 변수 혹은 `TEST_RECIPIENT` 환경 변수를 설정하세요.
 */
@SpringBootTest
class RealEmailSendTest {

    @Autowired
    private lateinit var sendEmailPort: SendEmailPort

    @Test
    // @EnabledIfEnvironmentVariable(named = "MAIL_USERNAME", matches = ".*") // 환경변수가 있을 때만 실행하려면 주석 해제
    fun `send actual email using env variables`() {
        // 환경 변수에서 수신자를 가져오거나, 하드코딩된 값 사용
        val to = System.getenv("TEST_RECIPIENT") ?: "dkstpwns0@gmail.com"
        val subject = "[Test] Gorogoro Notification Integration Test"
        val body = """
            This is a test email sent from Gorogoro Notification Service.
            If you see this, the SmtpMailAdapter is working correctly with your environment variables.
            
            Timestamp: ${System.currentTimeMillis()}
        """.trimIndent()

        println("Attempting to send email to: $to")
        
        try {
            sendEmailPort.sendEmail(to, subject, body)
            println("Email sent successfully!")
        } catch (e: Exception) {
            println("Failed to send email. Check your MAIL_USERNAME and MAIL_PASSWORD.")
            e.printStackTrace()
            throw e
        }
    }
}

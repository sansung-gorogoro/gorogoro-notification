package com.gorogoro.notification.rabbitmq.consumer

import com.gorogoro.notification.application.port.`in`.SendNotificationUseCase
import com.gorogoro.notification.domain.model.EmailType
import com.gorogoro.notification.domain.model.NotificationCommand
import com.gorogoro.notification.rabbitmq.config.RabbitMqConfig
import com.gorogoro.notification.rabbitmq.dto.NotificationEventDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

/**
 * RabbitMQ 통합 테스트
 *
 * 주의: 실행 전 RabbitMQ 서버가 구동되어 있어야 하며,
 * RABBIT_HOST, RABBIT_ID, RABBIT_PASSWORD 환경 변수가 설정되어 있어야 합니다.
 */
@SpringBootTest
class MessageConsumerIntegrationTest {

    @Autowired
    private lateinit var rabbitTemplate: RabbitTemplate

    // 실제 메일 발송 로직 대신 Mock을 사용하여 호출 여부만 검증
    @MockBean
    private lateinit var sendNotificationUseCase: SendNotificationUseCase

    @Test
    fun `RabbitMQ should consume message and invoke use case`() {
        // Given
        val event = NotificationEventDto(
            email = "integration-test@gorogoro.com",
            type = EmailType.WELCOME,
            payload = mapOf("username" to "tester")
        )

        // When
        // Exchange와 RoutingKey를 통해 메시지 발행
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.EXCHANGE_NAME,
            RabbitMqConfig.ROUTING_KEY,
            event
        )

        // Then
        // Consumer가 비동기로 동작하므로 일정 시간(5초) 동안 기다리며 호출 확인
        val captor = ArgumentCaptor.forClass(NotificationCommand::class.java)
        
        verify(sendNotificationUseCase, timeout(5000).times(1))
            .sendNotification(captor.capture())

        val command = captor.value
        assertEquals(event.email, command.email)
        assertEquals(event.type, command.type)
        assertEquals(event.payload["username"], command.payload["username"])
        
        println("SUCCESS: RabbitMQ message consumed and processed by UseCase mock.")
    }
}

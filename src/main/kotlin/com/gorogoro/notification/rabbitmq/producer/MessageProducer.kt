package com.gorogoro.notification.rabbitmq.producer

import com.gorogoro.notification.rabbitmq.config.RabbitMqConfig
import com.gorogoro.notification.rabbitmq.dto.NotificationEventDto
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class MessageProducer(
    private val rabbitTemplate: RabbitTemplate
) {
    fun send(event: NotificationEventDto) {
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.EXCHANGE_NAME,
            RabbitMqConfig.ROUTING_KEY,
            event
        )
    }
}

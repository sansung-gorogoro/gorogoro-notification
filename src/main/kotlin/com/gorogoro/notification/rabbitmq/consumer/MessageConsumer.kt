package com.gorogoro.notification.rabbitmq.consumer

import com.gorogoro.notification.application.port.`in`.SendNotificationUseCase
import com.gorogoro.notification.domain.model.NotificationCommand
import com.gorogoro.notification.rabbitmq.config.RabbitMqConfig
import com.gorogoro.notification.rabbitmq.dto.NotificationEventDto
import com.rabbitmq.client.Channel
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class MessageConsumer(
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val rabbitTemplate: RabbitTemplate
) {

    @RabbitListener(queues = [RabbitMqConfig.QUEUE_NAME], ackMode = "MANUAL")
    @Throws(IOException::class)
    fun handle(event: NotificationEventDto, message: Message, channel: Channel) {
        val tag = message.messageProperties.deliveryTag

        try {
            val command = NotificationCommand(
                email = event.email,
                type = event.type,
                payload = event.payload
            )

            val result = sendNotificationUseCase.sendNotification(command)

            // RPC Reply Logic: If there is a result (e.g., verification code) and a reply-to address
            if (result != null && message.messageProperties.replyTo != null) {
                val replyTo = message.messageProperties.replyTo
                val correlationId = message.messageProperties.correlationId

                val responseMessage = MessageBuilder
                    .withBody(result.toByteArray())
                    .setCorrelationId(correlationId)
                    .build()

                rabbitTemplate.send("", replyTo, responseMessage)
            }

            channel.basicAck(tag, false)
        } catch (ex: Exception) {
            channel.basicNack(tag, false, false)
        }
    }
}
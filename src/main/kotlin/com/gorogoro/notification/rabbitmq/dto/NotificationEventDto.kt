package com.gorogoro.notification.rabbitmq.dto

import com.gorogoro.notification.domain.model.EmailType

data class NotificationEventDto(
    val email: String,
    val type: EmailType,
    val payload: Map<String, Any> = emptyMap()
)

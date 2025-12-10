package com.gorogoro.notification.domain.model

data class NotificationCommand(
    val email: String,
    val type: EmailType,
    val payload: Map<String, Any> = emptyMap() // 확장성을 위해 payload 추가
)

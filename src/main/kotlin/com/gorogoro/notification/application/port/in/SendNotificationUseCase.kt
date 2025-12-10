package com.gorogoro.notification.application.port.`in`

import com.gorogoro.notification.domain.model.NotificationCommand

interface SendNotificationUseCase {
    fun sendNotification(command: NotificationCommand): String?
}

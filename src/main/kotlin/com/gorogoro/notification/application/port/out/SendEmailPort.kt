package com.gorogoro.notification.application.port.out

interface SendEmailPort {
    fun sendEmail(to: String, subject: String, body: String)
}

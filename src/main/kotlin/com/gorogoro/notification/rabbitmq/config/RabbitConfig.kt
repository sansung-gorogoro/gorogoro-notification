package com.gorogoro.notification.rabbitmq.config

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfig {

    companion object {
        const val QUEUE_NAME = "poc.spring.queue"
        const val QUEUE_NAME2 = "poc.spring.queue2"
        const val DLQ_NAME = "poc.spring.queue.dlq"

        const val EXCHANGE_NAME = "poc.spring.exchange"
        const val DLX_NAME = "poc.spring.dlx"

        const val ROUTING_KEY = "poc.spring.routing"
        const val DLQ_ROUTING_KEY = "poc.spring.routing.dlq"
    }

    @Bean
    fun pocQueue(): Queue {
        return QueueBuilder.durable(QUEUE_NAME)
            .withArgument("x-queue-type", "quorum")
            .withArgument("x-dead-letter-exchange", DLX_NAME)
            .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
            .build()
    }

    @Bean
    fun pocQueue2(): Queue {
        return QueueBuilder.durable(QUEUE_NAME2)
            .withArgument("x-queue-type", "quorum")
            .withArgument("x-dead-letter-exchange", DLX_NAME)
            .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
            .build()
    }

    @Bean
    fun pocDlq(): Queue = QueueBuilder.durable(DLQ_NAME).build()

    @Bean
    fun rabbitListenerContainerFactory(cf: ConnectionFactory): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(cf)
        factory.setConcurrentConsumers(3)
        factory.setMaxConcurrentConsumers(10)
        factory.setPrefetchCount(10)
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL)
        return factory
    }

    @Bean
    fun pocQueueBinding(pocQueue: Queue, pocExchange: TopicExchange): Binding {
        return BindingBuilder.bind(pocQueue).to(pocExchange).with(ROUTING_KEY)
    }

    @Bean
    fun pocQueue2Binding(pocQueue2: Queue, pocExchange: TopicExchange): Binding {
        return BindingBuilder.bind(pocQueue2).to(pocExchange).with(ROUTING_KEY)
    }

    @Bean
    fun pocDlqBinding(pocDlq: Queue, pocDlx: TopicExchange): Binding {
        return BindingBuilder.bind(pocDlq).to(pocDlx).with(DLQ_ROUTING_KEY)
    }

    @Bean
    fun pocExchange(): TopicExchange = TopicExchange(EXCHANGE_NAME)

    @Bean
    fun pocDlx(): TopicExchange = TopicExchange(DLX_NAME)

    @Bean
    fun jsonMessageConverter(): MessageConverter = Jackson2JsonMessageConverter()
}
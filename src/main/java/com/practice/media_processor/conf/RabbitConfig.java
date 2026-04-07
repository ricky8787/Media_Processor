package com.practice.media_processor.conf;

import org.springframework.amqp.core.*;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Value("${app.queue.name}")
    private String queueName;

    // 轉化器
    // @Bean
    // public MessageConverter messageConverter() {
    // return new Jackson2JsonMessageConverter();
    // }

    // // 定義交換機
    // @Bean
    // public DirectExchange directExchange() {
    // return new DirectExchange("direct-exchange", true, false);
    // }

    // 1. 定義死信佇列 (Dead Letter Queue)
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(queueName + ".dlq").build();
    }

    // 2. 定義死信交換機 (Dead Letter Exchange)
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(queueName + ".dlx");
    }

    // 3. 綁定 DLQ 與 DLX
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(queueName + ".routing.key");
    }

    // 4. 定義原本的佇列，並加上「毒藥訊息去哪裡」的參數設定
    @Bean
    public Queue imageQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", queueName + ".dlx")
                .withArgument("x-dead-letter-routing-key", queueName + ".routing.key")
                .build();
    }
}
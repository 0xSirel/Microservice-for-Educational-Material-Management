package it.unimol.newunimol.materialedidattico.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.materiale}")
    private String materialeExchange;

    @Value("${rabbitmq.queue.materiale.created}")
    private String materialeCreatedQueue;

    @Value("${rabbitmq.queue.materiale.updated}")
    private String materialeUpdatedQueue;

    @Value("${rabbitmq.routing-key.materiale.created}")
    private String materialeCreatedRoutingKey;

    @Value("${rabbitmq.routing-key.materiale.updated}")
    private String materialeUpdatedRoutingKey;

    @Value("${rabbitmq.queue.materiale.deleted}")
    private String materialeDeletedQueue;

    @Value("${rabbitmq.routing-key.materiale.deleted}")
    private String materialeDeletedRoutingKey;

    @Bean
    public Queue materialeDeletedQueue() {
        return QueueBuilder.durable(materialeDeletedQueue).build();
    }

    @Bean
    public Binding materialeDeletedBinding() {
        return BindingBuilder.bind(materialeDeletedQueue())
                .to(materialeExchange())
                .with(materialeDeletedRoutingKey);
    }


    @Bean
    public TopicExchange materialeExchange() {
        return new TopicExchange(materialeExchange);
    }

    @Bean
    public Queue materialeCreatedQueue() {
        return QueueBuilder.durable(materialeCreatedQueue).build();
    }

    @Bean
    public Queue materialeUpdatedQueue() {
        return QueueBuilder.durable(materialeUpdatedQueue).build();
    }

    @Bean
    public Binding materialeCreatedBinding() {
        return BindingBuilder.bind(materialeCreatedQueue())
                .to(materialeExchange())
                .with(materialeCreatedRoutingKey);
    }

    @Bean
    public Binding materialeUpdatedBinding() {
        return BindingBuilder.bind(materialeUpdatedQueue())
                .to(materialeExchange())
                .with(materialeUpdatedRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}

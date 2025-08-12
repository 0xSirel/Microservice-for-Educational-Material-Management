package it.unimol.newunimol.materialedidattico.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConsumerConfig {

    @Value("${rabbitmq.exchange.corso}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.corso.exist}")
    private String routingKey;

    @Value("${rabbitmq.queue.corso.exist}")
    private String queueName;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue queue() {
        return new Queue(queueName);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }
}

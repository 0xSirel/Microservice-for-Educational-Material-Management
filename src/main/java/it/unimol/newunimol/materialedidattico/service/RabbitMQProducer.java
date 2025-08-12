package it.unimol.newunimol.materialedidattico.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.materiale}")
    private String exchange;

    @Value("${rabbitmq.routing-key.materiale.created}")
    private String createdRoutingKey;

    @Value("${rabbitmq.routing-key.materiale.updated}")
    private String updatedRoutingKey;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMaterialeCreated(Object payload) {
        rabbitTemplate.convertAndSend(exchange, createdRoutingKey, payload);
    }

    public void sendMaterialeUpdated(Object payload) {
        rabbitTemplate.convertAndSend(exchange, updatedRoutingKey, payload);
    }
    @Value("${rabbitmq.routing-key.materiale.deleted}")
    private String deletedRoutingKey;

    public void sendMaterialeDeleted(Object payload) {
        rabbitTemplate.convertAndSend(exchange, deletedRoutingKey, payload);
    }

}

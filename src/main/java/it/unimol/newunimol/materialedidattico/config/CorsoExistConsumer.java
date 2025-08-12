package it.unimol.newunimol.materialedidattico.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CorsoExistConsumer {
    private static final Logger logger = LoggerFactory.getLogger(CorsoExistConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queue.corso.exist}")
    public void receiveMessage(String message) {
        logger.info("Messaggio ricevuto: {}", message);
    }
}

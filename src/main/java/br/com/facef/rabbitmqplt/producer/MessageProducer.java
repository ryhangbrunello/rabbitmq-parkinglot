package br.com.facef.rabbitmqplt.producer;

import static br.com.facef.rabbitmqplt.configuration.DirectExchangeConfiguration.ORDER_MESSAGES_QUEUE_NAME;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageProducer {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void sendFakeMessage() {
		log.info("Sending a fake message...");
		this.rabbitTemplate.convertAndSend(ORDER_MESSAGES_QUEUE_NAME,
				"FAKE-MESSAGE-".concat(LocalDateTime.now().toString()).concat(UUID.randomUUID().toString()));
	}
}

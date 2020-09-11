package br.com.facef.rabbitmqdlq.consumer;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import br.com.facef.rabbitmqdlq.configuration.DirectExchangeConfiguration;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MessageConsumer {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@RabbitListener(queues = DirectExchangeConfiguration.ORDER_MESSAGES_QUEUE_NAME)
	public void processOrderMessage(Message message) {
		log.info("Processing message: {}", message.toString());
		if (hasExceededRetryCount(message)) {
			putIntoParkingLot(message);
			return;
		}
		// By default the messages will be requeued
		throw new RuntimeException("Business Rule Exception");
		// To dont requeue message can throw AmqpRejectAndDontRequeueException
		// throw new AmqpRejectAndDontRequeueException("Business Rule Exception");
	}

	private boolean hasExceededRetryCount(Message in) {
		List<Map<String, ?>> xDeathHeader = in.getMessageProperties().getXDeathHeader();
		if (xDeathHeader != null && xDeathHeader.size() >= 1) {
			Long count = (Long) xDeathHeader.get(0).get("count");
			return count >= 3;
		}

		return false;
	}

	private void putIntoParkingLot(Message failedMessage) {
		log.info("Retries exeeded putting into parking lot");
		this.rabbitTemplate.send(DirectExchangeConfiguration.ORDER_MESSAGES_QUEUE_PARKINGLOT, failedMessage);
	}
}

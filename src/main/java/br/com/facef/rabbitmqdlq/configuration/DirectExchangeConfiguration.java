package br.com.facef.rabbitmqdlq.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectExchangeConfiguration {

	private static final String DIRECT_EXCHANGE_NAME = "order-exchange";

	public static final String ORDER_MESSAGES_QUEUE_NAME = "order-messages-queue";

	private static final String ORDER_MESSAGES_QUEUE_DLQ_NAME = ORDER_MESSAGES_QUEUE_NAME + ".dlq";

	public static final String ORDER_MESSAGES_QUEUE_PARKINGLOT = ORDER_MESSAGES_QUEUE_NAME + ".plt";

	private static final String PRIMARY_ROUTING_KEY = "primaryRoutingKey";

	@Bean
	DirectExchange exchange() {
		return ExchangeBuilder.directExchange(DIRECT_EXCHANGE_NAME).durable(true).build();
	}

	@Bean
	Queue orderMessagesQueue() {
		return QueueBuilder.durable(ORDER_MESSAGES_QUEUE_NAME).withArgument("x-dead-letter-exchange", "")
				.withArgument("x-dead-letter-routing-key", ORDER_MESSAGES_QUEUE_DLQ_NAME).build();
	}

	@Bean
	Queue orderMessagesDeadLetterQueue() {
		return QueueBuilder.durable(ORDER_MESSAGES_QUEUE_DLQ_NAME).deadLetterExchange(DIRECT_EXCHANGE_NAME)
				.deadLetterRoutingKey(PRIMARY_ROUTING_KEY).ttl(10000).build();
	}

	@Bean
	Queue parkinglotQueue() {
		return new Queue(ORDER_MESSAGES_QUEUE_PARKINGLOT);
	}

	@Bean
	Binding bindingOrderMessagesQueue(@Qualifier("orderMessagesQueue") Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(PRIMARY_ROUTING_KEY);
	}

	@Bean
	Binding bindingOrderMessagesDeadLetterQueue(@Qualifier("orderMessagesDeadLetterQueue") Queue queue,
			DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(ORDER_MESSAGES_QUEUE_DLQ_NAME);
	}

	@Bean
	Binding bindingParkinglotQueue(@Qualifier("parkinglotQueue") Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(ORDER_MESSAGES_QUEUE_PARKINGLOT);
	}
}

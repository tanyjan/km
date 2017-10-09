package com.test.schd;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMQTest implements Runnable {

	public final static String routingKey = "TEST_QUEUE";
	private RabbitTemplate template;
	private SourceFeads model;
	public RabbitMQTest(RabbitTemplate template, SourceFeads model) {
		this.template = template;
		this.model = model;
	}

	@Override
	public void run() {
		template.convertAndSend(routingKey, model);
	}

}

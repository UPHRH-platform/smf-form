package com.tarento.formservice.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FormServiceProducer {

	public static final Logger LOGGER = LoggerFactory.getLogger(FormServiceProducer.class);

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	public void pushToPipeline(Object object, String topic, String key) {
		LOGGER.info("Kafka Object : {}", object);
		LOGGER.info("Kafka Topic : {}", topic);
		LOGGER.info("Kafka Key : {}", key);
		kafkaTemplate.send(topic, key, object);
	}

}

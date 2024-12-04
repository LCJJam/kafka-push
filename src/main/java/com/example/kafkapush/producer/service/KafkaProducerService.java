package com.example.kafkapush.producer.service;

import com.example.kafkapush.PushNotification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
	private final KafkaTemplate<String, PushNotification> kafkaTemplate;

	@Value("${spring.kafka.topic.push-notifications}")
	private String topic;

	public KafkaProducerService(KafkaTemplate<String, PushNotification> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendPushNotification(PushNotification notification) {
		kafkaTemplate.send(topic, notification.getId(), notification);
		System.out.println("Produced message: " + notification);
	}
}

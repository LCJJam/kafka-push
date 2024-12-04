package com.example.kafkapush.producer.controller;

import com.example.kafkapush.PushNotification;
import com.example.kafkapush.producer.service.KafkaProducerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
	private final KafkaProducerService producerService;

	public NotificationController(KafkaProducerService producerService) {
		this.producerService = producerService;
	}

	@PostMapping
	public String sendNotification(@RequestBody PushNotification notification) {
		producerService.sendPushNotification(notification);
		return "Notification sent: " + notification.getId();
	}
}

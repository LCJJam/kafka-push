package com.example.kafkapush.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PushConsumer {

	private final FcmService fcmService;

	@KafkaListener(topics = "push_request", groupId = "push-worker")
	public void consume(ConsumerRecord<String, PushMessage> record) {
		PushMessage message = record.value();

		try {
			// FCM 발송
			fcmService.sendPush(message);
		} catch (Exception e) {
			// 실패 시 push_failure 토픽으로 이동
			handleFailure(message, e);
		}
	}

	private void handleFailure(PushMessage message, Exception e) {
		// 실패 처리 로직 (e.g., 로그 기록, 실패 토픽에 발행)
		System.err.println("Failed to send push: " + message.getDeviceToken());
	}
}

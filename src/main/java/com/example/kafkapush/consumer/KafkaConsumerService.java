package com.example.kafkapush.consumer;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.kafkapush.Platform;
import com.example.kafkapush.PushNotification;
import com.example.kafkapush.config.firebase.PushNotificationSender;

@Service
public class KafkaConsumerService {

	private final PushNotificationSender pushNotificationSender;

	public KafkaConsumerService(PushNotificationSender pushNotificationSender) {
		this.pushNotificationSender = pushNotificationSender;
	}

	@KafkaListener(
		topics = "${spring.kafka.topic.push-notifications}",
		groupId = "${spring.kafka.consumer.group-id}"
	)
	public void consumePushNotification(ConsumerRecord<String, PushNotification> record) {
		// Convert GenericRecord to PushNotification
		PushNotification notification = record.value();

		// 로그 출력
		System.out.println("Consumed PushNotification: " + notification);
		System.out.println("Schema Name: " + notification.getSchema().getName());

		// Push Notification 처리 로직
		try {
			if (notification.getPlatform() == Platform.FCM) {
				handleFCMNotification(notification);
			} else if (notification.getPlatform() == Platform.APNS) {
				handleAPNSNotification(notification);
			} else {
				System.err.println("Unsupported platform: " + notification.getPlatform());
			}
		} catch (Exception e) {
			System.err.println("Failed to process notification: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void handleFCMNotification(PushNotification notification) {
		try {
			// FCM 전송 로직
			pushNotificationSender.sendPushNotification(
				notification.getId(),
				notification.getDeviceToken(),
				notification.getTitle(),
				notification.getBody(),
				notification.getData()
			);
			System.out.println("FCM notification sent successfully.");
		} catch (Exception e) {
			System.err.println("Failed to send FCM notification: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void handleAPNSNotification(PushNotification notification) {
		try {
			// APNS 전송 로직
			// 현재는 FCM과 같은 방식으로 전송한다고 가정
			pushNotificationSender.sendPushNotification(
				notification.getDeviceToken(),
				notification.getTitle(),
				notification.getBody(),
				notification.getData(),
				notification.getSound()
			);
			System.out.println("APNS notification sent successfully.");
		} catch (Exception e) {
			System.err.println("Failed to send APNS notification: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

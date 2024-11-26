package com.example.kafkapush.consumer;

import java.net.http.HttpHeaders;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class FcmService {

	private final RestTemplate restTemplate;

	public void sendPush(PushMessage message) throws Exception {
		String fcmUrl = "https://fcm.googleapis.com/v1/projects/your-project-id/messages:send";
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth("your-fcm-server-key");
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> payload = Map.of(
			"message", Map.of(
				"token", message.getDeviceToken(),
				"notification", Map.of(
					"title", message.getTitle(),
					"body", message.getBody()
				),
				"data", Map.of(
					"actionUrl", message.getActionUrl()
				)
			)
		);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
		restTemplate.postForEntity(fcmUrl, request, String.class);
	}
}

package com.example.kafkapush.config.firebase;

import java.io.InputStream;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class PushNotificationSender {

	@Value("${push.fcm.url}")
	private String FCM_URL;

	@Value("${push.fcm.account-json}")
	private String accountFileName;

	public void sendPushNotification(String id, String deviceToken, String title, String body, String data) {
		try {
			// ClassPathResource로 resources/account.json 파일 읽기
			InputStream serviceAccountStream = new ClassPathResource(accountFileName).getInputStream();

			// OAuth2 Access Token
			GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
				.createScoped("https://www.googleapis.com/auth/firebase.messaging");
			credentials.refreshIfExpired();
			String accessToken = credentials.getAccessToken().getTokenValue();

			// Build payload
			JSONObject message = new JSONObject();
			JSONObject notification = new JSONObject();
			notification.put("title", title);
			notification.put("body", body);
			message.put("token", deviceToken);
			message.put("notification", notification);

			JSONObject payload = new JSONObject();
			payload.put("message", message);

			// HTTP request
			OkHttpClient client = new OkHttpClient();
			RequestBody requestBody = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
			Request request = new Request.Builder()
				.url(FCM_URL)
				.post(requestBody)
				.addHeader("Authorization", "Bearer " + accessToken)
				.addHeader("Content-Type", "application/json")
				.build();

			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				System.out.println("Push notification sent successfully.");
			} else {
				System.err.println("Failed to send push notification. HTTP Status: " + response.code());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

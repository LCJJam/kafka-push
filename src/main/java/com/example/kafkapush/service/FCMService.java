package com.example.kafkapush.service;

import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@Service
public class FCMService {

	@Value("${push.fcm.url}")
	private String fcmUrl;

	@Value("${push.fcm.server-key}")
	private String serverKey;

	private final OkHttpClient httpClient = new OkHttpClient();

	public void send(GenericRecord message) throws Exception {
		RequestBody body = RequestBody.create(
			"{\"to\":\"" + message.get("targetToken") + "\","
				+ "\"notification\":{\"title\":\"" + message.get("title") + "\","
				+ "\"body\":\"" + message.get("body") + "\"},"
				+ "\"data\":" + message.get("data") + "}",
			MediaType.get("application/json")
		);

		Request request = new Request.Builder()
			.url(fcmUrl)
			.post(body)
			.addHeader("Authorization", "key=" + serverKey)
			.build();

		try (Response response = httpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new RuntimeException("FCM message failed: " + response.body().string());
			}
			System.out.println("FCM message sent successfully!");
		}
	}
}

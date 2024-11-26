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
public class APNsService {

	@Value("${push.apns.url}")
	private String apnsUrl;

	@Value("${push.apns.auth-token}")
	private String authToken;

	private final OkHttpClient httpClient = new OkHttpClient();

	public void send(GenericRecord message) throws Exception {
		RequestBody body = RequestBody.create(
			"{\"aps\":{\"alert\":{\"title\":\"" + message.get("title") + "\","
				+ "\"body\":\"" + message.get("body") + "\"},"
				+ "\"badge\":" + message.get("badge") + ","
				+ "\"sound\":\"" + message.get("sound") + "\"}}",
			MediaType.get("application/json")
		);

		Request request = new Request.Builder()
			.url(apnsUrl + "/3/device/" + message.get("targetToken"))
			.post(body)
			.addHeader("Authorization", "bearer " + authToken)
			.build();

		try (Response response = httpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new RuntimeException("APNs message failed: " + response.body().string());
			}
			System.out.println("APNs message sent successfully!");
		}
	}
}

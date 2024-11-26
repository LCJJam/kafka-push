package com.example.kafkapush.processor;

import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Service;

import com.example.kafkapush.service.APNsService;
import com.example.kafkapush.service.FCMService;

@Service
public class PushProcessor {

	private final FCMService fcmService;
	private final APNsService apnsService;

	public PushProcessor(FCMService fcmService, APNsService apnsService) {
		this.fcmService = fcmService;
		this.apnsService = apnsService;
	}

	public void processFCMMessage(GenericRecord message) {
		try {
			System.out.println("Processing FCMMessage: " + message);
			fcmService.send(message);
		} catch (Exception e) {
			System.err.println("Error processing FCMMessage: " + e.getMessage());
		}
	}

	public void processAPNsMessage(GenericRecord message) {
		try {
			System.out.println("Processing APNsMessage: " + message);
			apnsService.send(message);
		} catch (Exception e) {
			System.err.println("Error processing APNsMessage: " + e.getMessage());
		}
	}
}

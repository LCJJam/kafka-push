package com.example.kafkapush.consumer;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.kafkapush.processor.PushProcessor;

@Service
public class KafkaConsumerService {

	private final PushProcessor pushProcessor;

	public KafkaConsumerService(PushProcessor pushProcessor) {
		this.pushProcessor = pushProcessor;
	}

	@KafkaListener(topics = "push-topic", groupId = "push-worker-group")
	public void consume(GenericRecord message) {
		String schemaName = message.getSchema().getName();
		System.out.println("Consumed message with schema: " + schemaName);

		switch (schemaName) {
			case "FCMMessage":
				pushProcessor.processFCMMessage(message);
				break;
			case "APNsMessage":
				pushProcessor.processAPNsMessage(message);
				break;
			default:
				System.err.println("Unsupported schema: " + schemaName);
		}
	}
}

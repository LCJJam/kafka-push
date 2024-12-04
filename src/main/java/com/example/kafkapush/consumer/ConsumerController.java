package com.example.kafkapush.consumer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

	@GetMapping("/api/consumer/status")
	public String consumerStatus() {
		return "Consumer is running!";
	}
}

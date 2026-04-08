package com.practice.media_processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MediaProcessorApplication {

	public static void main(String[] args) {
		System.out.println("BUILD_VERSION (before start) = " + System.getenv("BUILD_VERSION"));
		SpringApplication.run(MediaProcessorApplication.class, args);
	}

}

package org.sample;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SampleApplication {
	public static void main(String[] args) {
		ThreadContext.push("MAIN");
		SpringApplication.run(SampleApplication.class, args);
	}
}

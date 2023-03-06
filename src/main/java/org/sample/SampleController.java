package org.sample;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Controller
@Log4j2
public class SampleController {

	@GetMapping("sleep")
	@ResponseBody
	public ResponseEntity<SleepResponse> sleep(
			@RequestParam(required = false, defaultValue = "10") int seconds
	) {
		try {
			ThreadContext.push(RequestContextHolder.getRequestAttributes().getSessionId());
			if (seconds < 0) {
				return ResponseEntity.badRequest().header("reason", "'seconds' is less then zero").build();
			}
			Instant start = Instant.now();
			log.info("sleep {}", seconds);
			TimeUnit.SECONDS.sleep(seconds);
			Duration duration = Duration.between(start, Instant.now());
			log.info("ending [{}ms]", duration.toMillis());
			return ResponseEntity.ok(new SleepResponse(seconds, RequestContextHolder.getRequestAttributes().getSessionId()));
		} catch (InterruptedException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} finally {
			ThreadContext.pop();
		}
	}

	@Data
	private static class SleepResponse {
		private final int seconds;
		private final String sessionId;
	}
}

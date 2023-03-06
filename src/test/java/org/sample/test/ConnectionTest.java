package org.sample.test;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ConnectionTest {
	@Test
	public void test1() throws IOException, InterruptedException {
		ThreadContext.push("MAIN");
		log.info("test1");
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 5; i++) {
			pool.submit(new SleepRequest(i));
		}
		log.debug("ending submitting task");
		pool.shutdown();
		log.debug("shutdown - awaitTermination");
		while(!pool.awaitTermination(5, TimeUnit.SECONDS));
		log.debug("termination");
		ThreadContext.pop();
	}

	public static class SleepRequest implements Runnable {
		private final int i;

		public SleepRequest(int i) {
			this.i = i;
		}

		@Override
		public void run() {
			ThreadContext.push(Integer.toString(i));
			try {
				URL obj = new URL("http://localhost:8080/sleep");
				log.debug("Opening connection");
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				log.debug("Connection opened, setting GET method");
//				con.setConnectTimeout(5_000);
//				con.setReadTimeout(5_000);
				con.setRequestMethod("GET");
				log.info("Getting Response Code");
				int responseCode = con.getResponseCode();
				log.info("Response Code: {}", responseCode);
			} catch (Exception e) {
				log.error(e);
			} finally {
				ThreadContext.pop();
			}
		}
	}
}

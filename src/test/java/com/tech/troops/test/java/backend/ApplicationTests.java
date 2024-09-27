package com.tech.troops.test.java.backend;

import com.tech.troops.test.java.backend.service.EWalletService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private EWalletService eWalletService;

	@Test
	void testConcurrentTransactions() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		Long userId = 1L; // Assuming this user exists in the DB

		for (int i = 0; i < 100; i++) {
			executorService.submit(() -> {
				for (int j = 0; j < 1000; j++) {
					eWalletService.credit(userId, BigDecimal.valueOf(100));
				}
			});
		}

		executorService.shutdown();
		executorService.awaitTermination(15, TimeUnit.MINUTES);

		// Check the user's final balance (should be 10_000_000)
		assertEquals(BigDecimal.valueOf(10_000_000), eWalletService.getUserBalance(userId));
	}

}

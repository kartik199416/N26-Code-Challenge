package com.n26.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.n26.exception.ExpireTxException;
import com.n26.exception.UnparsableTxException;
import com.n26.model.Transaction;

@RunWith(SpringRunner.class)
public class TransactionServiceImplTest {

	@Autowired
	TransactionService transactionService;

	@Autowired
	StatisticsService statisticsService;

	@TestConfiguration
	static class TransactionServiceTestContextConfiguration {

		@Bean
		public TransactionServiceImpl transactionService() {
			return new TransactionServiceImpl();
		}

		@Bean
		public StatisticsServiceImpl statisticsService() {
			return new StatisticsServiceImpl();
		}

	}

	@Test
	public void testAddExpireTransaction() throws Exception {
		TransactionServiceImpl impl = (TransactionServiceImpl) transactionService;

		Instant now = Instant.now();
		Instant notStale = now.minusSeconds(impl.getExpireAfterSeconds() - 10);
		Instant stale = now.minusSeconds(impl.getExpireAfterSeconds()).minusMillis(1);

		// expect no exception
		Transaction tx = new Transaction(BigDecimal.ONE, notStale.toEpochMilli());
		transactionService.addTransaction(tx);

		// expect exception
		Transaction tx2 = new Transaction(BigDecimal.ONE, stale.toEpochMilli());
		assertThatExceptionOfType(ExpireTxException.class)
				.isThrownBy(() -> ((TransactionServiceImpl) transactionService).addTransaction(tx2));
	}

	@Test
	public void testAddFutureTransaction() throws Exception {
		Instant now = Instant.now();
		Instant future = now.plusSeconds(1);

		// expect no exception
		Transaction tx = new Transaction(BigDecimal.ONE, now.toEpochMilli());
		transactionService.addTransaction(tx);

		// expect exception
		Transaction tx2 = new Transaction(BigDecimal.ONE, future.toEpochMilli());
		assertThatExceptionOfType(UnparsableTxException.class)
				.isThrownBy(() -> ((TransactionServiceImpl) transactionService).addTransaction(tx2));
	}
}

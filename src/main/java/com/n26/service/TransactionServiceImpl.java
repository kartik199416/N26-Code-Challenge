package com.n26.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.n26.exception.ExpireTxException;
import com.n26.exception.UnparsableTxException;
import com.n26.model.Transaction;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Value("${transactionService.expire.interval.seconds:60}")
	private long expireInterval;

	@Autowired
	private StatsService statsService;

	@Override
	public void addTransaction(Transaction tx) {
		// TODO Auto-generated method stub
		Instant instant = Instant.now();
		if (tx.getTimestamp() - instant.toEpochMilli() > 0) {
			throw new UnparsableTxException();
		}
		if (tx.getTimestamp() - instant.minusSeconds(expireInterval).toEpochMilli() < 0) {
			throw new ExpireTxException();
		}
		statsService.register(tx);

	}

	@Override
	public void deleteTransaction() {
		// TODO Auto-generated method stub
		statsService.clearStatistics();
	}

}

package com.n26.service;

import com.n26.model.Transaction;

/**
 * 
 * @author Mr Kartik Parihar
 *
 */
public interface TransactionService {

	/**
	 * Add a transaction
	 * 
	 * @param transaction
	 */
	void addTransaction(Transaction transaction);

	/**
	 * Delete the transaction
	 */
	void deleteTransaction();
}

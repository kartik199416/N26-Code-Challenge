package com.n26.service;

import com.n26.exception.ExpireTxException;
import com.n26.exception.UnparsableTxException;
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
	 * @throws UnparsableTxException 
	 * @throws ExpireTxException 
	 */
	void addTransaction(Transaction transaction) throws UnparsableTxException, ExpireTxException;

	/**
	 * Delete the transaction
	 */
	void deleteTransaction();
}

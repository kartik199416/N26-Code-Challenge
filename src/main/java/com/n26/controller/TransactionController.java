package com.n26.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.n26.entity.Transaction;
import com.n26.service.TransactionService;

/**
 * REST API for transaction
 * 
 * @author Mr Kartik Parihar
 *
 */
@RestController("/transactions")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	/**
	 * Handle a new transaction request and also verify whether transaction
	 * falls in validity period.
	 * 
	 * @param transaction
	 *            {@link Transaction}
	 * @return {@link ResponseEntity}
	 */
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createTransaction(@RequestBody @Valid @NotNull Transaction transaction) {

		transactionService.addTransaction(transaction);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}

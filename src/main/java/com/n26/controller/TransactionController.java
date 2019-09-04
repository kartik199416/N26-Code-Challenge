package com.n26.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.*;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.n26.exception.ExpireTxException;
import com.n26.exception.UnparsableTxException;
import com.n26.model.Transaction;
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
	 * @throws ExpireTxException
	 * @throws UnparsableTxException
	 */
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createTransaction(@RequestBody @Valid @NotNull Transaction transaction)
			throws UnparsableTxException, ExpireTxException {

		transactionService.addTransaction(transaction);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@DeleteMapping()
	public ResponseEntity<Void> delete() {
		transactionService.deleteTransaction();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

  @ExceptionHandler({InvalidDefinitionException.class,JsonMappingException.class})
	public ResponseEntity<Void> handleJacksonMapping() {
		return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
}

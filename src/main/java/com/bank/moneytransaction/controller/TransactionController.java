package com.bank.moneytransaction.controller;

import com.bank.moneytransaction.exception.BadRequestException;
import com.bank.moneytransaction.entity.Transaction;
import com.bank.moneytransaction.request.MakeTransactionRequest;
import com.bank.moneytransaction.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/transaction")
public class TransactionController {

    @Autowired
    BusinessService businessService;

    @GetMapping(value = "/all")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return new ResponseEntity<>(businessService.getTransactions(), HttpStatus.OK);
    }

    @GetMapping(value = "/{accountNo}")
    public ResponseEntity<List<Transaction>> getAllTransactionsByAccountNo(@PathVariable Integer accountNo) {
        return new ResponseEntity<>(businessService.getTransactionsByAccountNo(accountNo), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Transaction> makeTransaction(@RequestBody MakeTransactionRequest transaction) throws BadRequestException {
        return new ResponseEntity<>(businessService.makeTransaction(transaction), HttpStatus.OK);
    }

}

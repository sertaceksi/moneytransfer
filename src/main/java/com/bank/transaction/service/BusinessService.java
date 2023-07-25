package com.bank.transaction.service;

import com.bank.transaction.errors.ErrorCodes;
import com.bank.transaction.exception.AccountNotFoundException;
import com.bank.transaction.exception.BadRequestException;
import com.bank.transaction.entity.Account;
import com.bank.transaction.entity.Transaction;
import com.bank.transaction.exception.InSufficientBalanceException;
import com.bank.transaction.controller.repository.AccountRepository;
import com.bank.transaction.controller.repository.TransactionRepository;
import com.bank.transaction.request.MakeTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BusinessService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AccountRepository accountRepository;

    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Transaction> getTransactionsByAccountNo(Integer accountNo) {

        Account account = accountRepository.findByAccNo(accountNo)
                .orElseThrow(AccountNotFoundException::new);
        return transactionRepository.findByAccountId(account.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public Transaction makeTransaction(MakeTransactionRequest transactionRequest) throws BadRequestException {

        //find an account by account no
        var senderAccount = accountRepository.findByAccNo(transactionRequest.getSenderAccountNo());
        senderAccount.ifPresentOrElse(account -> {
            sendMoney(transactionRequest, account);
        }, () -> {
            throw new AccountNotFoundException();
        });

        var receiverAccount = accountRepository.findByAccNo(transactionRequest.getRecipientAccountNo());

        receiverAccount.ifPresentOrElse(account -> {
            account.setBalance(account.getBalance() + transactionRequest.getAmount());
            accountRepository.save(account);
        }, () -> {
            throw new AccountNotFoundException();
        });

        if (!receiverAccount.get().getCurrency().getCode().equals(senderAccount.get().getCurrency().getCode())) {
            throw new BadRequestException(ErrorCodes.MSG_CODE_6.name(), "Sender and receiver account should be in same currency");
        }
        else if (receiverAccount.get().getAccNo().equals(senderAccount.get().getAccNo())) {
            throw new BadRequestException(ErrorCodes.MSG_CODE_7.name(), "Sender and receiver account should not be same");
        }

        Transaction tr = Transaction.builder()
                .senderAccount(senderAccount.get())
                .recipientAccount(receiverAccount.get())
                .amount(transactionRequest.getAmount())
                .createdAt(OffsetDateTime.now())
                .currency(receiverAccount.get().getCurrency())
                .build();

        return transactionRepository.save(tr);
    }

    private void sendMoney(MakeTransactionRequest transactionRequest, Account account) {
        if (account.getBalance() < transactionRequest.getAmount()) {
            throw new InSufficientBalanceException();
        }
        account.setBalance(account.getBalance() - transactionRequest.getAmount());
        accountRepository.save(account);
    }

    public List<Account> getAccountsByUserId(UUID userId) {
        return accountRepository.findByUserId(userId);
    }

}

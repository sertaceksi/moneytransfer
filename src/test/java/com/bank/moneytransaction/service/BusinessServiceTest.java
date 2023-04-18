package com.bank.moneytransaction.service;

import com.bank.moneytransaction.entity.User;
import com.bank.moneytransaction.exception.AccountNotFoundException;
import com.bank.moneytransaction.exception.BadRequestException;
import com.bank.moneytransaction.repository.UserRepository;
import com.bank.moneytransaction.entity.Account;
import com.bank.moneytransaction.entity.Currency;
import com.bank.moneytransaction.entity.Transaction;
import com.bank.moneytransaction.exception.InSufficientBalanceException;
import com.bank.moneytransaction.repository.AccountRepository;
import com.bank.moneytransaction.repository.CurrencyRepository;
import com.bank.moneytransaction.repository.TransactionRepository;
import com.bank.moneytransaction.request.MakeTransactionRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.bank.moneytransaction.util.CommonTestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessServiceTest {

    @InjectMocks
    private BusinessService businessService = new BusinessService();

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    private static Account account1;
    private static Account account2;
    private static Account account11;
    private static Account account22;
    private static Currency currency1;
    private static Currency currency2;


    @BeforeAll
    static void setUp() {
        currency1 = createCurrency("USD", "US Dollar");
        currency2 = createCurrency("GBP", "British Pound");
        //create a user
        User user1 = createUser();
        User user2 = createUser();
        //create accounts for the user
        account1 = createAccount(currency1, user1);
        account11 = createAccount(currency2, user1);
        account2 = createAccount(currency1, user2);
        account22 = createAccount(currency2, user2);
    }


    @Test
    void get_transactions() {
        when(transactionRepository.findAll()).thenReturn(List.of(createTransaction(currency1, account1, account2, 200),createTransaction(currency1, account11, account22, 300)));
        List<Transaction> transactions = businessService.getTransactions();
        Assertions.assertEquals(transactions.size(), 2);
    }

    @Test
    void get_transactions_by_accountId() {
        when(accountRepository.findByAccNo(account1.getAccNo())).thenReturn(Optional.of(account1));
        when(transactionRepository.findByAccountId(account1.getId())).thenReturn(List.of(createTransaction(currency1, account1, account2, 200)));

        List<Transaction> transactions = businessService.getTransactionsByAccountNo(account1.getAccNo());

        Assertions.assertEquals(transactions.size(), 1);
        Assertions.assertEquals(transactions.get(0).getSenderAccount().getAccNo(), account1.getAccNo());
        Assertions.assertEquals(transactions.get(0).getRecipientAccount().getAccNo(), account2.getAccNo());
        Assertions.assertEquals(transactions.get(0).getAmount(), 200);
    }

    @Test
    void get_transactions_by_accountId_account_not_found() {
        when(accountRepository.findByAccNo(account1.getAccNo())).thenReturn(Optional.empty());
        Assertions.assertThrows(AccountNotFoundException.class, () -> businessService.getTransactionsByAccountNo(account1.getAccNo()));
    }

    @Test
    void make_transaction_successful() throws BadRequestException {
        Integer account2Balance = account2.getBalance();
        Integer account1Balance = account1.getBalance();
        when(accountRepository.findByAccNo(account1.getAccNo())).thenReturn(Optional.of(account1));
        when(accountRepository.findByAccNo(account2.getAccNo())).thenReturn(Optional.of(account2));
        when(transactionRepository.save(any())).thenReturn(createTransaction(currency1, account1, account2, 100));
        MakeTransactionRequest makeTransactionRequest = buildMakeTransactionRequest(account1, account2, 100);

        Transaction transaction = businessService.makeTransaction(makeTransactionRequest);

        Assertions.assertEquals(transaction.getSenderAccount().getAccNo(), makeTransactionRequest.getSenderAccountNo());
        Assertions.assertEquals(transaction.getRecipientAccount().getAccNo(), makeTransactionRequest.getRecipientAccountNo());
        Assertions.assertEquals(transaction.getAmount(), makeTransactionRequest.getAmount());
        Assertions.assertEquals(transaction.getSenderAccount().getBalance(), account1Balance - makeTransactionRequest.getAmount());
        Assertions.assertEquals(transaction.getRecipientAccount().getBalance(), account2Balance + makeTransactionRequest.getAmount());
    }

    @Test
    void make_transaction_insufficient_balance() {
        when(accountRepository.findByAccNo(account1.getAccNo())).thenReturn(Optional.of(account1));

        MakeTransactionRequest makeTransactionRequest = buildMakeTransactionRequest(account1, account2, account1.getBalance() + 100);

        Assertions.assertThrows(InSufficientBalanceException.class, () -> {
            businessService.makeTransaction(makeTransactionRequest);
        });
    }

    @Test
    void make_transaction_sender_account_not_found() {
        when(accountRepository.findByAccNo(account1.getAccNo())).thenReturn(Optional.empty());

        MakeTransactionRequest makeTransactionRequest = buildMakeTransactionRequest(account1, account2, 100);

        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            businessService.makeTransaction(makeTransactionRequest);
        });
    }

    @Test
    void make_transaction_receiver_account_not_found() {
        when(accountRepository.findByAccNo(account1.getAccNo())).thenReturn(Optional.of(account1));
        when(accountRepository.findByAccNo(account2.getAccNo())).thenReturn(Optional.empty());

        MakeTransactionRequest makeTransactionRequest = buildMakeTransactionRequest(account1, account2, 100);

        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            businessService.makeTransaction(makeTransactionRequest);
        });
    }

    @Test
    void make_transaction_sender_and_receiver_account_same() {
        when(accountRepository.findByAccNo(account1.getAccNo())).thenReturn(Optional.of(account1));

        MakeTransactionRequest makeTransactionRequest = buildMakeTransactionRequest(account1, account1, 100);

        Assertions.assertThrows(BadRequestException.class, () -> {
            businessService.makeTransaction(makeTransactionRequest);
        });
    }

    @Test
    void make_transaction_sender_and_reciever_account_must_be_same_currency() {
        when(accountRepository.findByAccNo(account1.getAccNo())).thenReturn(Optional.of(account1));
        when(accountRepository.findByAccNo(account11.getAccNo())).thenReturn(Optional.of(account11));

        MakeTransactionRequest makeTransactionRequest = buildMakeTransactionRequest(account1, account11, 100);

        Assertions.assertThrows(BadRequestException.class, () -> {
            businessService.makeTransaction(makeTransactionRequest);
        });
    }


}
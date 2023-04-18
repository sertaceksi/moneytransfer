package com.bank.moneytransaction.controller;

import com.bank.moneytransaction.config.ErrorConfiguration;
import com.bank.moneytransaction.errors.Error;
import com.bank.moneytransaction.errors.ErrorCodes;
import com.bank.moneytransaction.errors.ErrorMessage;
import com.bank.moneytransaction.exception.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bank.moneytransaction.entity.Account;
import com.bank.moneytransaction.entity.Currency;
import com.bank.moneytransaction.entity.Transaction;
import com.bank.moneytransaction.entity.User;
import com.bank.moneytransaction.exception.AccountNotFoundException;
import com.bank.moneytransaction.request.MakeTransactionRequest;
import com.bank.moneytransaction.service.BusinessService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Map;

import static com.bank.moneytransaction.util.CommonTestUtils.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private ErrorConfiguration errorConfiguration;
    private static Account account1;
    private static Account account2;
    private static Account account11;
    private static Account account22;
    private static Currency currency1;

    @BeforeAll
    static void setUp() {
        Currency currency2;
        //create a user
        User user1 = createUser();
        User user2 = createUser();
        currency1 = createCurrency("USD", "US Dollar");
        currency2 = createCurrency("GBP", "British Pound");
        //create accounts for the user
        account1 = createAccount(currency1, user1);
        account11 = createAccount(currency2, user1);
        account2 = createAccount(currency1, user2);
        account22 = createAccount(currency2, user2);
    }

    @SneakyThrows
    @Test
    void get_all_transactions() {
        when(businessService.getTransactions())
                .thenReturn(List.of(createTransaction(currency1, account1, account2, 200)
                        , createTransaction(currency1, account11, account22, 300)));


        mockMvc.perform(get("/api/transaction/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @SneakyThrows
    @Test
    void get_all_transactions_by_account_no() {
        when(businessService.getTransactionsByAccountNo(account1.getAccNo()))
                .thenReturn(List.of(createTransaction(currency1, account1, account2, 200)
                        , createTransaction(currency1, account11, account22, 300)));

        mockMvc.perform(get("/api/transaction/{accountNo}", account1.getAccNo()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @SneakyThrows
    @Test
    void get_all_transactions_by_account_no_not_found() {
        when(businessService.getTransactionsByAccountNo(any())).thenThrow(new AccountNotFoundException());

        when(errorConfiguration.getErrorMessages()).thenReturn(Map.of("MSG_CODE_8", new ErrorMessage(new Error(), "", "404")));
        mockMvc.perform(get("/api/transaction/{accountNo}", 100))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void get_all_transactions_by_account_empty() {
        when(businessService.getTransactionsByAccountNo(account1.getAccNo()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/transaction/{accountNo}", account1.getAccNo()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @SneakyThrows
    @Test
    void make_transaction_success() {
        when(businessService.makeTransaction(any())).thenReturn(createTransaction(currency1, account1, account2, 200));
        Transaction tx = createTransaction(currency1, account1, account2, 200);

        mockMvc.perform(post("/api/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(MakeTransactionRequest.builder()
                                .senderAccountNo(account1.getAccNo())
                                .recipientAccountNo(account2.getAccNo())
                                .currency(currency1.getCode())
                                .amount(200)
                                .build())))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(tx.getAmount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.recipientAccount.accNo").value(tx.getRecipientAccount().getAccNo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.senderAccount.accNo").value(tx.getSenderAccount().getAccNo()));
    }

    @SneakyThrows
    @Test
    void make_transactions_account_not_found() {
        when(businessService.makeTransaction(any())).thenThrow(new AccountNotFoundException());
        when(errorConfiguration.getErrorMessages()).thenReturn(Map.of("MSG_CODE_8", new ErrorMessage(new Error(), "", "404")));

        mockMvc.perform(post("/api/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(MakeTransactionRequest.builder()
                                .senderAccountNo(account1.getAccNo())
                                .recipientAccountNo(account2.getAccNo())
                                .currency(currency1.getCode())
                                .amount(200)
                                .build())))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void make_transactions_bad_request_exception() {
        when(businessService.makeTransaction(any())).thenThrow(new BadRequestException(ErrorCodes.MSG_CODE_7.name(), "Sender and receiver account should not be same"));
        when(errorConfiguration.getErrorMessages()).thenReturn(Map.of("MSG_CODE_7", new ErrorMessage(new Error(), "", "400")));

        mockMvc.perform(post("/api/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(MakeTransactionRequest.builder()
                                .senderAccountNo(account1.getAccNo())
                                .recipientAccountNo(account2.getAccNo())
                                .currency(currency1.getCode())
                                .amount(200)
                                .build())))
                .andExpect(status().isBadRequest());
    }

}
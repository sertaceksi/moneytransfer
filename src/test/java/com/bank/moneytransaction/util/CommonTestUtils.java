package com.bank.moneytransaction.util;

import com.bank.moneytransaction.entity.Currency;
import com.bank.moneytransaction.entity.User;
import com.github.javafaker.Faker;
import com.bank.moneytransaction.entity.Account;
import com.bank.moneytransaction.entity.Transaction;
import com.bank.moneytransaction.request.MakeTransactionRequest;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class CommonTestUtils {

    public static final Faker faker = new Faker(new Locale("fi-FI"));

    public static Transaction createTransaction(Currency currency, Account senderAccount, Account recipientAccount, int amount) {
        return Transaction.builder()
                .amount(amount)
                .senderAccount(senderAccount)
                .recipientAccount(recipientAccount)
                .currency(currency)
                .createdAt(OffsetDateTime.now().minusDays(15L))
                .build();
    }

    public static MakeTransactionRequest buildMakeTransactionRequest(Account fromAccount, Account toAccount, int amount) {
        return MakeTransactionRequest.builder()
                .senderAccountNo(fromAccount.getAccNo())
                .recipientAccountNo(toAccount.getAccNo())
                .amount(amount)
                .build();
    }

    public static User createUser() {
        return User.builder()
                .phone(faker.phoneNumber().phoneNumber())
                .name(faker.name().name())
                .address(faker.address().fullAddress())
                .createdAt(OffsetDateTime.now().minusMonths(2L))
                .build();
    }

    public static Account createAccount(Currency currency, User user) {
        return Account.builder()
                .id(UUID.randomUUID())
                .accNo(faker.number().numberBetween(1000000000, 1999999999))
                .balance(new Random().nextInt(1000, 10000))
                .createdAt(OffsetDateTime.now().minusMonths(1L))
                .updatedAt(OffsetDateTime.now().minusMonths(1L))
                .currency(currency)
                .user(user)
                .build();
    }

    public static Currency createCurrency(String code, String name) {
        return Currency.builder()
                .code(code)
                .name(name)
                .build();
    }

}

package com.bank.moneytransaction.exception;

import lombok.Getter;

public class AccountNotFoundException extends RuntimeException{
    @Getter
    private final String errorCode;

    @Getter
    private final String errorCause;

    @Getter
    private final String message;

    public AccountNotFoundException() {
        this.errorCode = "MSG_CODE_8";
        this.errorCause = "Account not found";
        this.message = "Account not found";
    }
}

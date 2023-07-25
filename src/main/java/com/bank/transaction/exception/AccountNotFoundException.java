package com.bank.transaction.exception;

import com.bank.transaction.errors.ErrorCodes;
import lombok.Getter;

public class AccountNotFoundException extends RuntimeException{
    @Getter
    private final String errorCode;

    @Getter
    private final String errorCause;

    @Getter
    private final String message;

    public AccountNotFoundException() {
        this.errorCode = ErrorCodes.MSG_CODE_8.name();
        this.errorCause = "Account not found";
        this.message = "Account not found";
    }
}

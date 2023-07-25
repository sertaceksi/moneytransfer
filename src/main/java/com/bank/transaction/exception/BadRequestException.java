package com.bank.transaction.exception;

import lombok.Getter;

public class BadRequestException extends Exception {

    @Getter
    private final String errorCode;

    @Getter
    private final String errorCause;

    @Getter
    private final String message;

    public BadRequestException(String errorCode, String errorCause){
        this.errorCode = errorCode;
        this.errorCause=errorCause;
        message = null;
    }

}

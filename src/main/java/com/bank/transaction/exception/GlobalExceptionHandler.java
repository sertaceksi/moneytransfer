package com.bank.transaction.exception;

import com.bank.transaction.config.ErrorConfiguration;
import com.bank.transaction.errors.ErrorResponse;
import com.bank.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Autowired
    ErrorConfiguration errorConfiguration;

    Map<Integer, HttpStatus> httpStatusMap = Map.of(
            400, HttpStatus.BAD_REQUEST,
            401, HttpStatus.UNAUTHORIZED,
            404, HttpStatus.NOT_FOUND,
            500, HttpStatus.INTERNAL_SERVER_ERROR,
            503, HttpStatus.SERVICE_UNAVAILABLE
    );

    @ExceptionHandler(value = {BadRequestException.class})
    protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        log.error(ex.getMessage(), ex.getErrorCause());
        return generateErrorResponseEntity(ex.getErrorCode(), ex.getErrorCause());
    }

    @ExceptionHandler(value = {AccountNotFoundException.class})
    protected ResponseEntity<Object> handleBadRequestException(AccountNotFoundException ex) {
        log.error(ex.getMessage(), ex.getErrorCause());
        return generateErrorResponseEntity(ex.getErrorCode(), ex.getErrorCause());
    }

    private ResponseEntity<Object> generateErrorResponseEntity(String errorCode, String errorCause) {
        var errorMessage = errorConfiguration.getErrorMessages().get(errorCode);
        if (CommonUtils.isNullOrEmpty(errorCause)) {
            errorMessage.getError().setCause(errorCause);
        }

        var httpStatus = httpStatusMap.get(Integer.valueOf(errorMessage.getStatusCode()));

        return ResponseEntity.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.builder()
                        .httpCode(String.valueOf(httpStatus.value()))
                        .httpMessage(httpStatus.getReasonPhrase())
                        .errors(List.of(errorMessage.getError())).build());

    }


}

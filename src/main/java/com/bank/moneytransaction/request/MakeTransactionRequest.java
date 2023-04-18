package com.bank.moneytransaction.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MakeTransactionRequest {
    Integer recipientAccountNo;
    Integer amount;
    Integer senderAccountNo;
    String currency;
}

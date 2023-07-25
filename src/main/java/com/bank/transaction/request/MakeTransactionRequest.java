package com.bank.transaction.request;

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

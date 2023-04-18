package com.bank.moneytransaction.errors;

import lombok.*;

@Data
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Error {
    private String code;
    private String description;
    private String cause;
    private String resolution;
}

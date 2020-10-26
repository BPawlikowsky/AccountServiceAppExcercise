package com.AccountService.App.AccountServiceApp.Models.Requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class TransferMoneyRequest {

    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;
}

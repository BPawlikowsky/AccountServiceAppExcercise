package com.AccountService.App.AccountServiceApp.Models.Requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;

@AllArgsConstructor
@Getter
public class TransferMoneyRequest {

    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
}

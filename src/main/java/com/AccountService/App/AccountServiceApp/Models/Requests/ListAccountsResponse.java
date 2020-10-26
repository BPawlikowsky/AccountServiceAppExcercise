package com.AccountService.App.AccountServiceApp.Models.Requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;

@AllArgsConstructor
@Getter
public class ListAccountsResponse {

    private final String name;
    private final Currency currency;
    private final BigDecimal balance;
    private final boolean treasury;
    private final String status;
}

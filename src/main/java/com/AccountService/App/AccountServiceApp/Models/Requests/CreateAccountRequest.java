package com.AccountService.App.AccountServiceApp.Models.Requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.Currency;

@AllArgsConstructor
@Getter
public class CreateAccountRequest {

    private String name;
    private Currency currency;
    private BigDecimal balance;
    private boolean treasury;
}

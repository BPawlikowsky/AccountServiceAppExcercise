package com.AccountService.App.AccountServiceApp.Models.Requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@AllArgsConstructor
@Getter
public class CreateAccountRequest {

    private final String name;
    private final Currency currency;
    private final BigDecimal balance;
    private final boolean treasury;

    public CreateAccountRequest() {
        name = "";
        currency = null;
        balance = null;
        treasury = false;
    }
}

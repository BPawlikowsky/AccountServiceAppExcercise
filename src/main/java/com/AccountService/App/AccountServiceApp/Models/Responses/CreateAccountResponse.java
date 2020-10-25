package com.AccountService.App.AccountServiceApp.Models.Responses;

import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;

@AllArgsConstructor
@Getter
public class CreateAccountResponse {

    private final String name;
    private final Currency currency;
    private final BigDecimal balance;
    private final boolean treasury;
    private final String status;

    public CreateAccountResponse(String status, CreateAccountRequest request) {
        this.name = request.getName();
        this.currency = request.getCurrency();
        this.balance = request.getBalance();
        this.treasury = request.isTreasury();
        this.status = status;
    }

}

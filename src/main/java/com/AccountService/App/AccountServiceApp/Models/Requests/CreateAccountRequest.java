package com.AccountService.App.AccountServiceApp.Models.Requests;

import lombok.Getter;
import org.javamoney.moneta.Money;

import java.util.Currency;

@Getter
public class CreateAccountRequest {

    private String name;
    private Currency currency;
    private Money money;
    private boolean treasury;
}

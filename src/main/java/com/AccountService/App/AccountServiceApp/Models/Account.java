package com.AccountService.App.AccountServiceApp.Models;

import org.javamoney.moneta.Money;

import javax.persistence.*;
import java.util.Currency;

@Entity
@Table
public class Account {

    @Id @GeneratedValue
    private long id;

    @Column
    private String name;
    @Column
    private Currency currency;
    @Column
    private Money balance;
    @Column
    private Boolean treasury;

    public Account(String name, Currency currency, Money balance, boolean treasury) {
        this.name = name;
        this.currency = currency;
        this.balance = balance;
        this.treasury = treasury;
    }
}

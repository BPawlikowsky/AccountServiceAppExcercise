package com.AccountService.App.AccountServiceApp.Models;

import javax.persistence.*;
import java.math.BigDecimal;
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
    private BigDecimal balance;
    @Column
    private Boolean treasury;

    public Account(String name, Currency currency, BigDecimal balance, boolean treasury) {
        this.name = name;
        this.currency = currency;
        this.balance = balance;
        this.treasury = treasury;
    }
}

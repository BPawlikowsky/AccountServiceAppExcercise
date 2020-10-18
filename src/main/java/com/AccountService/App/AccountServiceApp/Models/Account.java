package com.AccountService.App.AccountServiceApp.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Table
@NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private long id;

    @Getter@Setter
    @Column
    private String name;
    @Getter@Setter
    @Column
    private Currency currency;
    @Getter@Setter
    @Column
    private BigDecimal balance;
    @Getter
    @Column
    private Boolean treasury;

    public Account(String name, Currency currency, BigDecimal balance, boolean treasury) {
        this.name = name;
        this.currency = currency;
        this.balance = balance;
        this.treasury = treasury;
    }
}

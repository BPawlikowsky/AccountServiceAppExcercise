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
    private String Name;
    @Column
    private Currency currency;
    @Column
    private Money balance;
    @Column
    private Boolean treasury;
}

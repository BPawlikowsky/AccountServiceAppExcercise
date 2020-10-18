package com.AccountService.App.AccountServiceApp.Service;

import com.AccountService.App.AccountServiceApp.Models.Account;
import com.AccountService.App.AccountServiceApp.Models.AccountsRepository;
import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

    @Autowired
    private AccountsRepository accountsRepository;

    public boolean createAccount(CreateAccountRequest request) {
        System.out.println(
                request.getName() +
                        request.getCurrency() +
                        request.getBalance().intValue() +
                        request.isTreasury()
        );
        Account newAccount = new Account(
                request.getName(),
                request.getCurrency(),
                Money.of(request.getBalance(), request.getCurrency().getCurrencyCode()),
                request.isTreasury()
        );
        accountsRepository.save(newAccount);
        return true;
    }
}

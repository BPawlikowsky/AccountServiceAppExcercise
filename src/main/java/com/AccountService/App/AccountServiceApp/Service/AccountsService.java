package com.AccountService.App.AccountServiceApp.Service;

import com.AccountService.App.AccountServiceApp.Models.AccountsRepository;
import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
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
                        request.getMoney() + request.isTreasury()
        );
        return true;
    }
}

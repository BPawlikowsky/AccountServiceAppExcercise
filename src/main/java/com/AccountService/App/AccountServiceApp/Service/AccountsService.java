package com.AccountService.App.AccountServiceApp.Service;

import com.AccountService.App.AccountServiceApp.Models.Account;
import com.AccountService.App.AccountServiceApp.Models.AccountsRepository;
import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
import com.AccountService.App.AccountServiceApp.Models.Requests.TransferMoneyRequest;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

@Service
public class AccountsService {

    @Autowired
    private AccountsRepository accountsRepository;

    public boolean createAccount(CreateAccountRequest request) {
        if(request.getName().equals(""))
            return false;
        else if(request.getCurrency() == null)
            return false;
        else if(request.getBalance() == null)
            return false;
        else {
            Account newAccount = new Account(
                    request.getName(),
                    request.getCurrency(),
                    request.getBalance(),
                    request.isTreasury()
            );
            accountsRepository.save(newAccount);
            return true;
        }
    }

    public boolean transferMoney(TransferMoneyRequest request) {
        Account fromAccount = accountsRepository.findByName(request.getFromAccount()).get(0);
        Account toAccount = accountsRepository.findByName(request.getToAccount()).get(0);
        Money fromMoney = Money.of(fromAccount.getBalance(), fromAccount.getCurrency().getCurrencyCode());
        Money toMoney = Money.of(toAccount.getBalance(), toAccount.getCurrency().getCurrencyCode());

        //Subtract from sending account
        MonetaryAmount amountToSubstract = Monetary.getDefaultAmountFactory()
                .setNumber(request.getAmount())
                .setCurrency(fromMoney.getCurrency())
                .create();
        fromMoney = fromMoney.subtract(amountToSubstract);
        fromAccount.setBalance(fromMoney.getNumberStripped());

        //Add to receiving account
        MonetaryAmount amountToAdd = Monetary.getDefaultAmountFactory()
                .setNumber(request.getAmount())
                .setCurrency(toMoney.getCurrency())
                .create();
        toMoney = toMoney.add(amountToAdd);
        toAccount.setBalance(toMoney.getNumberStripped());

        //Persist
        accountsRepository.save(fromAccount);
        accountsRepository.save(toAccount);

        return true;
    }
}

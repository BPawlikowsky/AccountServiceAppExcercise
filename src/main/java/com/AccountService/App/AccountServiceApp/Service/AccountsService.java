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
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.util.Currency;
import java.util.List;

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
        Account fromAccount, toAccount;
        //Check if accounts exist
        try {
            fromAccount = accountsRepository.findByName(request.getFromAccount()).get(0);
            toAccount = accountsRepository.findByName(request.getToAccount()).get(0);
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("Account not found: " + e.getMessage());
            return false;
        }
        //Check if valid amount
        if(request.getAmount() != null && request.getAmount().intValue() < 0)
            return false;

        //Check if transaction is legal for given from account
        if(
                !fromAccount.getTreasury() &&
                (fromAccount.getBalance().getNumberStripped().subtract(request.getAmount()).intValue()) < 0
        )
            return false;
        Money fromMoney = Money.of(fromAccount.getBalance().getNumber(), fromAccount.getCurrency().getCurrencyCode());
        Money toMoney = Money.of(toAccount.getBalance().getNumber(), toAccount.getCurrency().getCurrencyCode());

        //Subtract from sending account
        MonetaryAmount amountToSubstract = Monetary.getDefaultAmountFactory()
                .setNumber(request.getAmount())
                .setCurrency(fromMoney.getCurrency())
                .create();
        fromMoney = fromMoney.subtract(amountToSubstract);
        fromAccount.setBalance(fromMoney.getNumberStripped());

        //Currency Conversion
        MonetaryAmount amountToAdd = Monetary.getDefaultAmountFactory()
                .setNumber(request.getAmount())
                .setCurrency(fromMoney.getCurrency())
                .create();

        CurrencyConversion conversion = MonetaryConversions.getConversion(toMoney.getCurrency());
        MonetaryAmount convertedAmount = amountToAdd.with(conversion);
        //Add to receiving account
        toMoney = toMoney.add(convertedAmount);
        toAccount.setBalance(toMoney.getNumberStripped());

        //Persist
        accountsRepository.save(fromAccount);
        accountsRepository.save(toAccount);

        return true;
    }

    public List<Account> getAllAccounts() {
        return accountsRepository.findAll();
    }

    public List<Account> findAccountByName(String name) {
        return accountsRepository.findByName(name);
    }

    public List<Account> findAccountByCurrency(String currency) {
        return accountsRepository.findByCurrency(Currency.getInstance(currency));
    }

    public List<Account> findAccountByTreasury(Boolean treasury) {
        return accountsRepository.findByTreasury(treasury);
    }
}

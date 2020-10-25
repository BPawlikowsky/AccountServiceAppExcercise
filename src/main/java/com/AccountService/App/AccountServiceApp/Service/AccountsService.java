package com.AccountService.App.AccountServiceApp.Service;

import com.AccountService.App.AccountServiceApp.Models.Account;
import com.AccountService.App.AccountServiceApp.Models.AccountsRepository;
import com.AccountService.App.AccountServiceApp.Models.Exceptions.CreateAccountException;
import com.AccountService.App.AccountServiceApp.Models.Exceptions.TransferMoneyException;
import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
import com.AccountService.App.AccountServiceApp.Models.Requests.TransferMoneyRequest;
import com.AccountService.App.AccountServiceApp.Models.Responses.CreateAccountResponse;
import com.AccountService.App.AccountServiceApp.Models.Responses.TransferMoneyResponse;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(AccountsService.class);

    @Autowired
    private AccountsRepository accountsRepository;

    public CreateAccountResponse createAccount(CreateAccountRequest request) throws CreateAccountException {
        String status;

        if(request.getName() == null || request.getName().equals("")) {
            status = "No name";
            throw new CreateAccountException(status);
        }
        else if(request.getCurrency() == null) {
            status = "No currency";
            throw new CreateAccountException(status);
        }
        else if(request.getBalance() == null) {
            status = "No balance";
            throw new CreateAccountException(status);
        }
        else {
            Account newAccount = new Account(
                    request.getName(),
                    request.getCurrency(),
                    request.getBalance(),
                    request.isTreasury()
            );
            accountsRepository.save(newAccount);
            status = "Account created";
        }
        return new CreateAccountResponse(status, request);
    }

    public TransferMoneyResponse transferMoney(TransferMoneyRequest request) throws TransferMoneyException {
        String status;
        Account fromAccount, toAccount;
        try {
            fromAccount = accountsRepository.findByName(request.getFromAccount()).get(0);
            toAccount = accountsRepository.findByName(request.getToAccount()).get(0);
        }
        catch (IndexOutOfBoundsException e) {
            fromAccount = toAccount = null;
            logger.debug("transferMoney(): " + e.getMessage());
        }
        //Check if accounts exist
        if(fromAccount == null || toAccount == null) {
            status = "One of the accounts doesn't exist.";
            throw new TransferMoneyException(status);
        }
        //Check if valid amount
        else if(request.getAmount() != null && request.getAmount().intValue() < 0) {
            status = "Amount is a negative value.";
            throw new TransferMoneyException(status);
        }
        //Check if transaction is legal for given from account
        else if(
                !fromAccount.getTreasury() &&
                (fromAccount.getBalance().getNumberStripped().subtract(request.getAmount()).intValue()) < 0
        ) {
            status = "Transaction is illegal for a given fromAccount.";
            throw new TransferMoneyException(status);
        }
        //Checks passed
        else {
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

            status = "Transaction successful.";
        }
        return new TransferMoneyResponse(status, request);
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

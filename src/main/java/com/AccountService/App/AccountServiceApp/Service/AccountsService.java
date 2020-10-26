package com.AccountService.App.AccountServiceApp.Service;

import com.AccountService.App.AccountServiceApp.Models.Account;
import com.AccountService.App.AccountServiceApp.Models.AccountsRepository;
import com.AccountService.App.AccountServiceApp.Exceptions.AccountsListException;
import com.AccountService.App.AccountServiceApp.Exceptions.CreateAccountException;
import com.AccountService.App.AccountServiceApp.Exceptions.TransferMoneyException;
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
import java.util.ArrayList;
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
            MonetaryAmount amountToSubtract = Monetary.getDefaultAmountFactory()
                    .setNumber(request.getAmount())
                    .setCurrency(fromMoney.getCurrency())
                    .create();
            fromMoney = fromMoney.subtract(amountToSubtract);
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

    public List<Account> getAllAccounts() throws AccountsListException {
        List<Account> list = accountsRepository.findAll();
        if(list.size() == 0)
            throw new AccountsListException("No accounts in database.");
        return list;
    }

    public List<Account> findAccountByName(String name) throws AccountsListException {
        List<Account> list = accountsRepository.findByName(name);
        if(list.size() == 0)
            throw new AccountsListException("Couldn't find account " + name + " in the database.");
        return list;
    }

    public List<Account> findAccountByCurrency(String currency) throws AccountsListException {
        List<Account> list = new ArrayList<>(0);
        try {
            list = accountsRepository.findByCurrency(Currency.getInstance(currency));
        }
        catch (IllegalArgumentException e) {
            logger.debug("findAccountByCurrency(): " + e.getMessage());
            throw new AccountsListException("Couldn't identify currency.");
        }
        if(list.size() == 0)
            throw new AccountsListException("Couldn't find currency " + currency + " in the database.");
        return list;
    }

    public List<Account> findAccountByTreasury(String treasury) throws AccountsListException {
        if(treasury.equals("") ||
                (!treasury.equals("0") && !treasury.equals("1") &&
                !treasury.toLowerCase().equals("true") &&
                !treasury.toLowerCase().equals("false"))
        ) {
            throw new AccountsListException("Wrong value, not boolean(true, false, 1, 0).");
        }
        List<Account> list = accountsRepository.findByTreasury(Boolean.valueOf(treasury));
        if(list.size() == 0)
            throw new AccountsListException("Couldn't find treasury accounts in the database.");
        return list;
    }
}

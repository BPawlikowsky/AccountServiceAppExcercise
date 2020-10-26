package com.AccountService.App.AccountServiceApp.Controller;

import com.AccountService.App.AccountServiceApp.Models.Account;
import com.AccountService.App.AccountServiceApp.Exceptions.AccountsListException;
import com.AccountService.App.AccountServiceApp.Exceptions.CreateAccountException;
import com.AccountService.App.AccountServiceApp.Exceptions.TransferMoneyException;
import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
import com.AccountService.App.AccountServiceApp.Models.Requests.TransferMoneyRequest;
import com.AccountService.App.AccountServiceApp.Models.Responses.CreateAccountResponse;
import com.AccountService.App.AccountServiceApp.Models.Responses.TransferMoneyResponse;
import com.AccountService.App.AccountServiceApp.Service.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class AccountsController {

    @Autowired
    private AccountsService accountsService;

    @PostMapping("/accounts")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<CreateAccountResponse> createAccount(@RequestBody CreateAccountRequest request)
            throws CreateAccountException {
        CreateAccountResponse response;
        response = accountsService.createAccount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/accounts")
    @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<List<Account>> listAllAccounts() throws AccountsListException {
        List<Account> listAll = accountsService.getAllAccounts();
        if(listAll.size() != 0)
            return new ResponseEntity<>(listAll, HttpStatus.FOUND);
        else
            return new ResponseEntity<>(listAll, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/accounts/findByName/{name}")
    @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<List<Account>> findByName(@PathVariable String name) throws AccountsListException {
        List<Account> listByName = accountsService.findAccountByName(name);
        if(listByName.size() != 0)
            return new ResponseEntity<>(listByName, HttpStatus.FOUND);
        else
            return new ResponseEntity<>(listByName, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/accounts/findByCurrency/{currency}")
    @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<List<Account>> findByCurrency(@PathVariable String currency) throws AccountsListException {
        List<Account> listByCurrency = accountsService.findAccountByCurrency(currency);
        if(listByCurrency.size() != 0)
            return new ResponseEntity<>(listByCurrency, HttpStatus.FOUND);
        else
            return new ResponseEntity<>(listByCurrency, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/accounts/findByTreasury/{treasury}")
    @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<List<Account>> findByTreasury(@PathVariable String treasury) throws AccountsListException {
        List<Account> listByTreasury = accountsService.findAccountByTreasury(treasury);
        if(listByTreasury.size() != 0)
            return new ResponseEntity<>(listByTreasury, HttpStatus.FOUND);
        else
            return new ResponseEntity<>(listByTreasury, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/accounts/transfer")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    @ResponseBody
    public ResponseEntity<TransferMoneyResponse> transferMoney(@RequestBody TransferMoneyRequest request)
    throws TransferMoneyException {
        TransferMoneyResponse response = accountsService.transferMoney(request);
        if(response.getStatus().equals("Transaction successful."))
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        else
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }
}

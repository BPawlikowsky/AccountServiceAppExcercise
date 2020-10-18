package com.AccountService.App.AccountServiceApp.Controller;

import com.AccountService.App.AccountServiceApp.Models.Account;
import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
import com.AccountService.App.AccountServiceApp.Models.Requests.TransferMoneyRequest;
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
    public ResponseEntity<HttpStatus> createAccount(@RequestBody CreateAccountRequest request) {
        if(accountsService.createAccount(request))
            return new ResponseEntity<>(HttpStatus.CREATED);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/accounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Account> listAllAccounts() {
        return accountsService.getAllAccounts();
    }

    @GetMapping("/accounts/findByName/{name}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Account> findByName(@PathVariable String name) {
        return accountsService.findAccountByName(name);
    }

    @GetMapping("/accounts/findByCurrency/{currency}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Account> findByCurrency(@PathVariable String currency) {
        return accountsService.findAccountByCurrency(currency);
    }

    @GetMapping("/accounts/findByTreasury/{treasury}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Account> findByTreasury(@PathVariable Boolean treasury) {
        return accountsService.findAccountByTreasury(treasury);
    }

    @PostMapping("/accounts/transfer")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ResponseEntity<HttpStatus> transferMoney(@RequestBody TransferMoneyRequest request) {
        if(accountsService.transferMoney(request))
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        else
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
}

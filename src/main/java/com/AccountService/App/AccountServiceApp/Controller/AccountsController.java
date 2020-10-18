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
    public ResponseEntity<List<Account>> listAllAccounts() {
        List<Account> listAll = accountsService.getAllAccounts();
        if(listAll.size() != 0)
            return new ResponseEntity<List<Account>>(listAll, HttpStatus.FOUND);
        else
            return new ResponseEntity<List<Account>>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/accounts/findByName/{name}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<List<Account>> findByName(@PathVariable String name) {
        List<Account> listByName = accountsService.findAccountByName(name);
        if(listByName.size() != 0)
            return new ResponseEntity<List<Account>>(listByName, HttpStatus.FOUND);
        else return new ResponseEntity<List<Account>>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/accounts/findByCurrency/{currency}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<List<Account>> findByCurrency(@PathVariable String currency) {
        List<Account> listByCurrency = accountsService.findAccountByCurrency(currency);
        if(listByCurrency.size() != 0)
            return new ResponseEntity<List<Account>>(listByCurrency, HttpStatus.FOUND);
        else return new ResponseEntity<List<Account>>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/accounts/findByTreasury/{treasury}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<List<Account>> findByTreasury(@PathVariable Boolean treasury) {
        List<Account> listByTreasury = accountsService.findAccountByTreasury(treasury);
        if(listByTreasury.size() != 0)
            return new ResponseEntity<List<Account>>(listByTreasury, HttpStatus.FOUND);
        else return new ResponseEntity<List<Account>>(HttpStatus.NOT_FOUND);
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

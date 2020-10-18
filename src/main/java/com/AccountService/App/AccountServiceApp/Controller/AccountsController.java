package com.AccountService.App.AccountServiceApp.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountsController {

    @PostMapping("/accounts")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<HttpStatus> createAccount() {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

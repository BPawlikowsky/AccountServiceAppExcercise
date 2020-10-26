package com.AccountService.App.AccountServiceApp.Exceptions;

import lombok.Getter;

public class CreateAccountException extends RuntimeException {
    @Getter
    private final String details;

    public CreateAccountException(String details) {
        super(details);
        this.details = details;
    }
}

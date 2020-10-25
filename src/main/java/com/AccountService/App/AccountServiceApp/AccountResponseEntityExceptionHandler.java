package com.AccountService.App.AccountServiceApp;

import com.AccountService.App.AccountServiceApp.Models.Exceptions.CreateAccountException;
import com.AccountService.App.AccountServiceApp.Models.Exceptions.TransferMoneyException;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.context.request.WebRequest;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AccountResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(AccountResponseEntityExceptionHandler.class);

    @ExceptionHandler(CreateAccountException.class)
    protected ResponseEntity<Object> handleAccountCreation(CreateAccountException ex, WebRequest request) {
        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("error", "accerr");
        responseBody.addProperty("message", "Account request: " + ex.getMessage());
        responseBody.addProperty("details", "The request body was incorrectly formatted or empty.");
        responseBody.addProperty("path", "/accounts");
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        String bodyOfResponse = responseBody.toString();
        return handleExceptionInternal(ex, bodyOfResponse, header, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(TransferMoneyException.class)
    protected ResponseEntity<Object> handleTransferMoney(TransferMoneyException ex, WebRequest request) {
        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("error", "transerr");
        responseBody.addProperty("message", "Transfer request failed");
        responseBody.addProperty("details", ex.getMessage());
        responseBody.addProperty("path", "/accounts/transfer");
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        String bodyOfResponse = responseBody.toString();
        return handleExceptionInternal(ex, bodyOfResponse, header, HttpStatus.BAD_REQUEST, request);
    }
}

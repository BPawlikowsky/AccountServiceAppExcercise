package com.AccountService.App.AccountServiceApp.Exceptions;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.context.request.ServletWebRequest;
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
        responseBody.addProperty("path", ((ServletWebRequest)request).getRequest().getRequestURI());
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
        responseBody.addProperty("path", ((ServletWebRequest)request).getRequest().getRequestURI());
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        String bodyOfResponse = responseBody.toString();
        return handleExceptionInternal(ex, bodyOfResponse, header, HttpStatus.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler(AccountsListException.class)
    protected ResponseEntity<Object> handleTransferMoney(AccountsListException ex, WebRequest request) {
        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("error", "acclist");
        responseBody.addProperty("message", "List accounts request failed");
        responseBody.addProperty("details", ex.getMessage());
        responseBody.addProperty("path", ((ServletWebRequest)request).getRequest().getRequestURI());
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        String bodyOfResponse = responseBody.toString();
        return handleExceptionInternal(ex, bodyOfResponse, header, HttpStatus.NOT_FOUND, request);
    }
}

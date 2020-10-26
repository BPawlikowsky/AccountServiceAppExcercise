package com.AccountService.App.AccountServiceApp.Controller;

import com.AccountService.App.AccountServiceApp.Exceptions.CreateAccountException;
import com.AccountService.App.AccountServiceApp.Models.Account;
import com.AccountService.App.AccountServiceApp.Models.AccountsRepository;
import com.AccountService.App.AccountServiceApp.Exceptions.AccountsListException;
import com.AccountService.App.AccountServiceApp.Exceptions.TransferMoneyException;
import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
import com.AccountService.App.AccountServiceApp.Models.Requests.TransferMoneyRequest;
import com.AccountService.App.AccountServiceApp.Models.Responses.CreateAccountResponse;
import com.AccountService.App.AccountServiceApp.Models.Responses.TransferMoneyResponse;
import com.AccountService.App.AccountServiceApp.Service.AccountsService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AccountsControllerTest {

    private List<Account> accountList;

    @Spy
    private AccountsService accountsService;

    @Mock
    private AccountsRepository accountsRepository;

    @InjectMocks
    private AccountsController accountsController;

    @BeforeEach
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void createTestAccounts() {
        Account fromTestAccount = new Account(
                "fromTest",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                false
        );
        Account toTestAccount = new Account(
                "toTest",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                false
        );
        accountsRepository.save(toTestAccount);
        accountsRepository.save(fromTestAccount);
    }

    @BeforeEach
    public void prepareAccountList() {
        Account fromTestAccount = new Account(
                "fromTest",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                false
        );
        Account toTestAccount = new Account(
                "toTest",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                false
        );
        accountList = Arrays.asList(fromTestAccount, toTestAccount);
    }

    @Test
    public void createAccount_OK() {
        CreateAccountRequest request = new CreateAccountRequest(
                "Test1",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        CreateAccountResponse response = new CreateAccountResponse("Account created", request);
        ResponseEntity<CreateAccountResponse> expectedResponse = new ResponseEntity<>(response, HttpStatus.CREATED);

        doReturn(response).when(accountsService).createAccount(request);
        ResponseEntity<CreateAccountResponse> actualResponse = accountsController.createAccount(request);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void createAccount_BAD_REQUEST_No_Name() {
        CreateAccountRequest request = new CreateAccountRequest(
                null,
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );

        assertThrows(CreateAccountException.class, () -> accountsController.createAccount(request));
    }

    @Test
    public void createAccount_BAD_REQUEST_No_Currency() {
        CreateAccountRequest request = new CreateAccountRequest(
                "Test01",
                null,
                BigDecimal.valueOf(1000),
                true
        );

        assertThrows(CreateAccountException.class, () -> accountsController.createAccount(request));
    }

    @Test
    public void createAccount_BAD_REQUEST_No_Balance() {
        CreateAccountRequest request = new CreateAccountRequest(
                "Test01",
                Currency.getInstance("EUR"),
                null,
                true
        );
        assertThrows(CreateAccountException.class, () -> accountsController.createAccount(request));
    }



    @Test
    public void listAllAccounts_FOUND() throws AccountsListException {
        ResponseEntity<List<Account>> expectedResponse = new ResponseEntity<>(accountList, HttpStatus.FOUND);

        doReturn(accountList).when(accountsService).getAllAccounts();
        ResponseEntity<List<Account>> actualResponse = accountsController.listAllAccounts();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void listAllAccounts_NOT_FOUND() throws AccountsListException {
        doThrow(AccountsListException.class).when(accountsService).getAllAccounts();
        assertThrows(AccountsListException.class, () -> accountsController.listAllAccounts());
    }

    @Test
    public void findByName_FOUND() throws AccountsListException {
        ResponseEntity<List<Account>> expectedResponse = new ResponseEntity<>(List.of(accountList.get(0)), HttpStatus.FOUND);
        String request = "fromTest";

        doReturn(List.of(accountList.get(0))).when(accountsService).findAccountByName(request);
        ResponseEntity<List<Account>> actualResponse = accountsController.findByName(request);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void findByName_NOT_FOUND() throws AccountsListException {
        String request = "Test01";
        doThrow(AccountsListException.class).when(accountsService).findAccountByName(request);
        assertThrows(AccountsListException.class, () -> accountsController.findByName(request));
    }

    @Test
    public void findByCurrency_FOUND() throws AccountsListException {
        ResponseEntity<List<Account>> expectedResponse = new ResponseEntity<>(accountList, HttpStatus.FOUND);
        String request = "EUR";
        doReturn(accountList).when(accountsService).findAccountByCurrency(request);
        ResponseEntity<List<Account>> actualResponse = accountsController.findByCurrency(request);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void findByCurrency_NOT_FOUND() throws AccountsListException {
        String request = "EUR";
        doThrow(AccountsListException.class).when(accountsService).findAccountByCurrency(request);
        assertThrows(AccountsListException.class, () -> accountsController.findByCurrency(request));
    }

    @Test
    public void findByTreasury_FOUND() throws AccountsListException {
        ResponseEntity<List<Account>> expectedResponse = new ResponseEntity<>(accountList, HttpStatus.FOUND);

        String request = "false";
        doReturn(accountList).when(accountsService).findAccountByTreasury(request);
        ResponseEntity<List<Account>> actualResponse = accountsController.findByTreasury(request);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void findByTreasury_NOT_FOUND() throws AccountsListException {
        String request = "false";
        doThrow(AccountsListException.class).when(accountsService).findAccountByTreasury(request);
        assertThrows(AccountsListException.class, () -> accountsController.findByTreasury(request));
    }

    @Test
    public void transferMoney_ACCEPTED() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(100)
        );
        TransferMoneyResponse response = new TransferMoneyResponse("Transaction successful.", request);
        ResponseEntity<TransferMoneyResponse> expectedResponse = new ResponseEntity<>(response, HttpStatus.ACCEPTED);

        doReturn(response).when(accountsService).transferMoney(request);
        ResponseEntity<TransferMoneyResponse> actualResponse = accountsController.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void transferMoney_NOT_ACCEPTABLE() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(1100)
        );
        TransferMoneyResponse response = new TransferMoneyResponse("Transaction is illegal for a given fromAccount.", request);
        ResponseEntity<TransferMoneyResponse> expectedResponse = new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);

        doReturn(response).when(accountsService).transferMoney(request);
        ResponseEntity<TransferMoneyResponse> actualResponse = accountsController.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }
}
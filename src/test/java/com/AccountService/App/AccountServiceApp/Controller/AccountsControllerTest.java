package com.AccountService.App.AccountServiceApp.Controller;

import com.AccountService.App.AccountServiceApp.Models.Account;
import com.AccountService.App.AccountServiceApp.Models.AccountsRepository;
import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
import com.AccountService.App.AccountServiceApp.Models.Requests.TransferMoneyRequest;
import com.AccountService.App.AccountServiceApp.Models.Responses.CreateAccountResponse;
import com.AccountService.App.AccountServiceApp.Service.AccountsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AccountsControllerTest {

    private List<Account> accountList;

    @Mock
    private AccountsService accountsService;

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private AccountsController accountsController;

    @Test
    public void createAccount() {
        CreateAccountRequest request = new CreateAccountRequest(
                "Test1",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        CreateAccountResponse response = new CreateAccountResponse("Account created", request);
        ResponseEntity<CreateAccountResponse> expectedResponse = new ResponseEntity<>(response, HttpStatus.CREATED);

        when(accountsController.createAccount(request)).thenReturn(expectedResponse);
        ResponseEntity<CreateAccountResponse> actualResponse = accountsController.createAccount(request);
        assertEquals(expectedResponse, actualResponse);
    }

    @Before
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

    @Before
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
    public void listAllAccounts() {
        ResponseEntity<List<Account>> expectedResponse = new ResponseEntity<>(accountList, HttpStatus.FOUND);

        when(accountsController.listAllAccounts()).thenReturn(expectedResponse);
        ResponseEntity<List<Account>> actualResponse = accountsController.listAllAccounts();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void findByName() {
        ResponseEntity<List<Account>> expectedResponse = new ResponseEntity<>(List.of(accountList.get(0)), HttpStatus.FOUND);

        when(accountsController.findByName("fromTest")).thenReturn(expectedResponse);
        ResponseEntity<List<Account>> actualResponse = accountsController.findByName("fromTest");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void findByCurrency() {
        ResponseEntity<List<Account>> expectedResponse = new ResponseEntity<>(accountList, HttpStatus.FOUND);

        when(accountsController.findByCurrency("EUR")).thenReturn(expectedResponse);
        ResponseEntity<List<Account>> actualResponse = accountsController.findByCurrency("EUR");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void findByTreasury() {
        ResponseEntity<List<Account>> expectedResponse = new ResponseEntity<>(accountList, HttpStatus.FOUND);

        when(accountsController.findByTreasury(false)).thenReturn(expectedResponse);
        ResponseEntity<List<Account>> actualResponse = accountsController.findByTreasury(false);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void transferMoney() {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(100)
        );
        ResponseEntity<HttpStatus> expectedResponse = new ResponseEntity<>(HttpStatus.ACCEPTED);

        when(accountsController.transferMoney(request)).thenReturn(expectedResponse);
        ResponseEntity<HttpStatus> actualResponse = accountsController.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }
}
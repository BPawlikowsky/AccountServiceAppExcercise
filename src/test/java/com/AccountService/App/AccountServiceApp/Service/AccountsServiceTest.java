package com.AccountService.App.AccountServiceApp.Service;

import com.AccountService.App.AccountServiceApp.Controller.AccountsController;
import com.AccountService.App.AccountServiceApp.Models.*;
import com.AccountService.App.AccountServiceApp.Models.Responses.*;
import com.AccountService.App.AccountServiceApp.Models.Requests.*;
import com.AccountService.App.AccountServiceApp.Models.Exceptions.*;
import com.AccountService.App.AccountServiceApp.AccountResponseEntityExceptionHandler;
import org.assertj.core.util.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class AccountsServiceTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    private List<Account> accountList;

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private AccountsService accountsService;

    @InjectMocks
    private AccountsController accountsController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("When valid request is received, return true")
    public void createAccount_Created() {
        CreateAccountRequest request = new CreateAccountRequest(
                "Test1",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        CreateAccountResponse expectedResponse = new CreateAccountResponse("Account created", request);
        when(accountsService.createAccount(request)).thenReturn(expectedResponse);
        CreateAccountResponse actualResponse = accountsService.createAccount(request);
        assertEquals(expectedResponse, actualResponse);
    }

    @Before
    public void setupExceptionHandler() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountsController)
                .setControllerAdvice(new AccountResponseEntityExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("When no name is received, return false")
    public void createAccount_Not_Created() {
        CreateAccountRequest request = new CreateAccountRequest(
                "",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        String status = "No name";
        CreateAccountResponse expectedResponse = new CreateAccountResponse(status, request);

        when(accountsService.createAccount(request))
                .thenThrow(new CreateAccountException(status))
                .thenReturn(expectedResponse);
        exception.expect(CreateAccountException.class);
        CreateAccountResponse actualResponse = accountsService.createAccount(request);
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
    @Test
    @DisplayName("When valid moneyTransferRequest is received, return true")
    public void transferMoney_Accepted() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(100)
        );

        TransferMoneyResponse expectedResponse = new TransferMoneyResponse("Transaction successful.", request);

        when(accountsService.transferMoney(request)).thenReturn(expectedResponse);
        TransferMoneyResponse actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    @DisplayName("When wrong fromAccount is received")
    public void transferMoney_WrongFromAccount() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "",
                "toTest",
                BigDecimal.valueOf(100)
        );

        TransferMoneyResponse expectedResponse = new TransferMoneyResponse("One of the accounts doesn't exist.", request);

        when(accountsService.transferMoney(request)).thenReturn(expectedResponse);
        TransferMoneyResponse actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    @DisplayName("When wrong toAccount is recieved, return false")
    public void transferMoney_WrongToAccount() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "",
                BigDecimal.valueOf(100)
        );

        TransferMoneyResponse expectedResponse = new TransferMoneyResponse("One of the accounts doesn't exist.", request);

        when(accountsService.transferMoney(request)).thenReturn(expectedResponse);
        TransferMoneyResponse actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    @DisplayName("When wrong Amount is recieved, return false")
    public void transferMoney_WrongAmount() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(-100)
        );

        TransferMoneyResponse expectedResponse = new TransferMoneyResponse("Amount is a negative value.", request);

        when(accountsService.transferMoney(request)).thenReturn(expectedResponse);
        TransferMoneyResponse actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    @DisplayName("When non treasury account try's to transfer too many funds, return false")
    public void transferMoney_NonTreasuryNegative() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(1100)
        );

        TransferMoneyResponse expectedResponse =
                new TransferMoneyResponse("Transaction is illegal for a given fromAccount.", request);

        when(accountsService.transferMoney(request)).thenReturn(expectedResponse);
        TransferMoneyResponse actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }

    @Before
    public void createTreasuryTestAccounts() {
        Account fromTestAccount = new Account(
                "fromTest",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        Account toTestAccount = new Account(
                "toTest",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        accountsRepository.save(toTestAccount);
        accountsRepository.save(fromTestAccount);
    }
    @Test
    @DisplayName("When non treasury account try's to transfer too many funds, return false")
    public void transferMoney_TreasuryNegative() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(1100)
        );

        TransferMoneyResponse expectedResponse =
                new TransferMoneyResponse("Transaction is illegal for a given fromAccount.", request);

        when(accountsService.transferMoney(request)).thenReturn(expectedResponse);
        TransferMoneyResponse actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
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
    @DisplayName("When findAll received and database as fields, non empty list")
    public void findAll() {
        List<Account> expectedResponse = accountList;
        when(accountsService.getAllAccounts()).thenReturn(accountList);

        List<Account> actualResponse = accountsService.getAllAccounts();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("When findAll recieved and database empty, return empty list")
    public void findAll_NotFound() {
        List<Account> expectedResponse = Lists.emptyList();
        when(accountsService.getAllAccounts()).thenReturn(Lists.emptyList());

        List<Account> actualResponse = accountsService.getAllAccounts();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("When findByName found, return Non empty List")
    public void findByName_Found() {
        List<Account> expectedResponse = List.of(accountList.get(0));
        when(accountsService.findAccountByName("fromTest")).thenReturn(expectedResponse);

        List<Account> actualResponse = accountsService.findAccountByName("fromTest");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("When findByName not found, return empty List")
    public void findByName_NotFound() {
        List<Account> expectedResponse = Lists.emptyList();
        when(accountsService.findAccountByName("TestX")).thenReturn(expectedResponse);

        List<Account> actualResponse = accountsService.findAccountByName("TestX");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("When findByCurrency found, return Non empty List")
    public void findByCurrency_Found() {
        List<Account> expectedResponse = accountList;
        when(accountsService.findAccountByCurrency("EUR")).thenReturn(expectedResponse);

        List<Account> actualResponse = accountsService.findAccountByCurrency("EUR");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("When findByCurrency found, return empty List")
    public void findByCurrency_NotFound() {
        List<Account> expectedResponse = Lists.emptyList();
        when(accountsService.findAccountByCurrency("USD")).thenReturn(expectedResponse);

        List<Account> actualResponse = accountsService.findAccountByCurrency("USD");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("When findByCurrency found, return Non empty List")
    public void findByTreasury_Found() {
        List<Account> expectedResponse = accountList;
        when(accountsService.findAccountByTreasury(true)).thenReturn(expectedResponse);

        List<Account> actualResponse = accountsService.findAccountByTreasury(true);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("When findByCurrency found, return empty List")
    public void findByTreasury_NotFound() {
        List<Account> expectedResponse = Lists.emptyList();
        when(accountsService.findAccountByTreasury(false)).thenReturn(expectedResponse);

        List<Account> actualResponse = accountsService.findAccountByTreasury(false);
        assertEquals(expectedResponse, actualResponse);
    }
}
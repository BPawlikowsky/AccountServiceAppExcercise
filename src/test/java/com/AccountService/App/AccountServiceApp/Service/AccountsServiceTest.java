package com.AccountService.App.AccountServiceApp.Service;

import com.AccountService.App.AccountServiceApp.Models.Account;
import com.AccountService.App.AccountServiceApp.Models.AccountsRepository;
import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
import com.AccountService.App.AccountServiceApp.Models.Requests.TransferMoneyRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AccountsServiceTest {

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private AccountsService accountsService;

    @Test
    @DisplayName("When valid request is received, return true")
    public void createAccount_Created() {
        CreateAccountRequest request = new CreateAccountRequest(
                "Test1",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        Boolean expectedResponse = Boolean.TRUE;

        when(accountsService.createAccount(request)).thenReturn(Boolean.TRUE);
        Boolean actualResponse = accountsService.createAccount(request);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("When valid request is received, return false")
    public void createAccount_Not_Created() {
        CreateAccountRequest request = new CreateAccountRequest(
                "Test1",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        Boolean expectedResponse = Boolean.FALSE;

        when(accountsService.createAccount(request)).thenReturn(Boolean.FALSE);
        Boolean actualResponse = accountsService.createAccount(request);
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
    @DisplayName("When valid moneyTrasferRequest is recieved, return true")
    public void transferMoney_Accepted() {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(100)
        );

        Boolean expectedResponse = Boolean.TRUE;

        when(accountsService.transferMoney(request)).thenReturn(Boolean.TRUE);
        Boolean actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    @DisplayName("When wrong fromAccount is recieved, return false")
    public void transferMoney_WrongFromAccount() {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "",
                "toTest",
                BigDecimal.valueOf(100)
        );

        Boolean expectedResponse = Boolean.FALSE;

        when(accountsService.transferMoney(request)).thenReturn(Boolean.FALSE);
        Boolean actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    @DisplayName("When wrong toAccount is recieved, return false")
    public void transferMoney_WrongToAccount() {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "",
                BigDecimal.valueOf(100)
        );

        Boolean expectedResponse = Boolean.FALSE;

        when(accountsService.transferMoney(request)).thenReturn(Boolean.FALSE);
        Boolean actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    @DisplayName("When wrong Amount is recieved, return false")
    public void transferMoney_WrongAmount() {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(-100)
        );

        Boolean expectedResponse = Boolean.FALSE;

        when(accountsService.transferMoney(request)).thenReturn(Boolean.FALSE);
        Boolean actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    @DisplayName("When non treasury account try's to transfer too many funds, return false")
    public void transferMoney_NonTreasuryNegative() {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(1100)
        );

        Boolean expectedResponse = Boolean.FALSE;

        when(accountsService.transferMoney(request)).thenReturn(Boolean.FALSE);
        Boolean actualResponse = accountsService.transferMoney(request);
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
    public void transferMoney_TreasuryNegative() {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(1100)
        );

        Boolean expectedResponse = Boolean.TRUE;

        when(accountsService.transferMoney(request)).thenReturn(Boolean.TRUE);
        Boolean actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse, actualResponse);
    }
}
package com.AccountService.App.AccountServiceApp.Service;

import com.AccountService.App.AccountServiceApp.Models.AccountsRepository;
import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
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
}
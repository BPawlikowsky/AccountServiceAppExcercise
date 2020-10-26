package com.AccountService.App.AccountServiceApp.Service;
import com.AccountService.App.AccountServiceApp.Models.*;
import com.AccountService.App.AccountServiceApp.Models.Responses.*;
import com.AccountService.App.AccountServiceApp.Models.Requests.*;
import com.AccountService.App.AccountServiceApp.Exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AccountsServiceTest {

    Logger logger = LoggerFactory.getLogger(AccountsServiceTest.class);

    private Account fromTestAccount, toTestAccount;
    private List<Account> accountList;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private AccountsService accountsService;

    @BeforeEach
    public void createTestAccounts() {
        fromTestAccount = new Account(
                "fromTest",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                false
        );
        toTestAccount = new Account(
                "toTest",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                false
        );
        accountsRepository.save(fromTestAccount);
        accountsRepository.save(toTestAccount);
        accountList = Arrays.asList(fromTestAccount, toTestAccount);
    }

    @AfterEach
    public void cleanup() {
        accountsRepository.delete(fromTestAccount);
        accountsRepository.delete(toTestAccount);
    }

    @Test
    @DisplayName("When valid request is received")
    public void createAccount_Created() {
        CreateAccountRequest request = new CreateAccountRequest(
                "Test1",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        CreateAccountResponse expectedResponse = new CreateAccountResponse("Account created", request);
        CreateAccountResponse actualResponse = accountsService.createAccount(request);
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(3, accountsRepository.findAll().size());
        assertEquals("Test1", accountsRepository.findByName("Test1").get(0).getName());
        accountsRepository.delete(accountsRepository.findByName("Test1").get(0));
    }

    @Test
    @DisplayName("When no name is received")
    public void createAccount_Failed_No_Name() {
        CreateAccountRequest request = new CreateAccountRequest(
                "",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        assertThrows(CreateAccountException.class,() -> accountsService.createAccount(request));
    }

    @Test
    @DisplayName("When no currency is received")
    public void createAccount_Failed_No_currency() {
        CreateAccountRequest request = new CreateAccountRequest(
                "Test01",
                null,
                BigDecimal.valueOf(1000),
                true
        );
        assertThrows(CreateAccountException.class,() -> accountsService.createAccount(request));
    }

    @Test
    @DisplayName("When no balance is received")
    public void createAccount_Failed_No_balance() {
        CreateAccountRequest request = new CreateAccountRequest(
                "Test01",
                Currency.getInstance("EUR"),
                null,
                true
        );
        assertThrows(CreateAccountException.class,() -> accountsService.createAccount(request));
    }

    @Test
    @DisplayName("When valid moneyTransferRequest is received")
    public void transferMoney_Accepted() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(100)
        );

        TransferMoneyResponse expectedResponse = new TransferMoneyResponse("Transaction successful.", request);

        TransferMoneyResponse actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(
                BigDecimal.valueOf(900).toString(),
                accountsRepository.findByName("fromTest").get(0).getBalance().getNumberStripped().toPlainString()
        );
        assertEquals(
                BigDecimal.valueOf(1100).toString(),
                accountsRepository.findByName("toTest").get(0).getBalance().getNumberStripped().toPlainString()
        );
    }
    @Test
    @DisplayName("When wrong fromAccount is received")
    public void transferMoney_WrongFromAccount() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "",
                "toTest",
                BigDecimal.valueOf(100)
        );
        assertThrows(TransferMoneyException.class, () -> accountsService.transferMoney(request));
    }

    @Test
    @DisplayName("When wrong toAccount is received")
    public void transferMoney_WrongToAccount() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "",
                BigDecimal.valueOf(100)
        );
        assertThrows(TransferMoneyException.class, () -> accountsService.transferMoney(request));
    }

    @Test
    @DisplayName("When wrong Amount is received")
    public void transferMoney_WrongAmount() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(-100)
        );
        assertThrows(TransferMoneyException.class, () -> accountsService.transferMoney(request));
    }

    @Test
    @DisplayName("When non treasury account try's to transfer too many funds")
    public void transferMoney_NonTreasuryNegative() throws TransferMoneyException {
        TransferMoneyRequest request = new TransferMoneyRequest(
                "fromTest",
                "toTest",
                BigDecimal.valueOf(1100)
        );
        assertThrows(TransferMoneyException.class, () -> accountsService.transferMoney(request));
    }

    @Test
    @DisplayName("When non treasury account try's to transfer too many funds")
    public void transferMoney_TreasuryNegative() throws TransferMoneyException {
        Account treasuryTest = new Account(
                "treasuryTest",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        accountsRepository.save(treasuryTest);
        TransferMoneyRequest request = new TransferMoneyRequest(
                "treasuryTest",
                "toTest",
                BigDecimal.valueOf(1100)
        );

        TransferMoneyResponse expectedResponse =
                new TransferMoneyResponse("Transaction successful.", request);

        TransferMoneyResponse actualResponse = accountsService.transferMoney(request);
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(
                BigDecimal.valueOf(-100).toString(),
                accountsRepository.findByName("treasuryTest").get(0).getBalance().getNumberStripped().toPlainString()
        );
        accountsRepository.delete(treasuryTest);
    }

    @Test
    @DisplayName("When findAll received and database as fields, non empty list")
    public void findAll() throws AccountsListException {
        List<Account> actualResponse = accountsService.getAllAccounts();
        for(int i = 0; i < actualResponse.size(); i++) {
            assertEquals(accountList.get(i).getName(), actualResponse.get(i).getName());
            assertEquals(accountList.get(i).getCurrency(), actualResponse.get(i).getCurrency());
            assertEquals(accountList.get(i).getBalance(), actualResponse.get(i).getBalance());
            assertEquals(accountList.get(i).getTreasury(), actualResponse.get(i).getTreasury());
        }
    }

    @Test
    @DisplayName("When findAll recieved and database empty, return empty list")
    public void findAll_NotFound() {
        accountsRepository.delete(fromTestAccount);
        accountsRepository.delete(toTestAccount);
        assertThrows(AccountsListException.class, () -> accountsService.getAllAccounts());
    }

    @Test
    @DisplayName("When findByName found, return Non empty List")
    public void findByName_Found() throws AccountsListException {
        List<Account> expectedResponse = List.of(accountList.get(0));

        List<Account> actualResponse = accountsService.findAccountByName("fromTest");
        assertEquals(accountList.get(0).getName(), actualResponse.get(0).getName());
        assertEquals(accountList.get(0).getCurrency(), actualResponse.get(0).getCurrency());
        assertEquals(accountList.get(0).getBalance(), actualResponse.get(0).getBalance());
        assertEquals(accountList.get(0).getTreasury(), actualResponse.get(0).getTreasury());
    }

    @Test
    @DisplayName("When findByName not found, return empty List")
    public void findByName_NotFound() {
        assertThrows(AccountsListException.class,() -> accountsService.findAccountByName("TestX"));
    }

    @Test
    @DisplayName("When findByCurrency found, return Non empty List")
    public void findByCurrency_Found() throws AccountsListException {
        List<Account> actualResponse = accountsService.findAccountByCurrency("EUR");
        for(int i = 0; i < actualResponse.size(); i++) {
            assertEquals(accountList.get(i).getName(), actualResponse.get(i).getName());
            assertEquals(accountList.get(i).getCurrency(), actualResponse.get(i).getCurrency());
            assertEquals(accountList.get(i).getBalance(), actualResponse.get(i).getBalance());
            assertEquals(accountList.get(i).getTreasury(), actualResponse.get(i).getTreasury());
        }
    }

    @Test
    @DisplayName("When findByCurrency found")
    public void findByCurrency_NotFound() {
       assertThrows(AccountsListException.class, () -> accountsService.findAccountByCurrency("USD"));
    }

    @Test
    @DisplayName("When findByTreasury found, return Non empty List")
    public void findByTreasury_Found() throws AccountsListException {
        List<Account> actualResponse = accountsService.findAccountByTreasury("false");
        for(int i = 0; i < actualResponse.size(); i++) {
            assertEquals(accountList.get(i).getName(), actualResponse.get(i).getName());
            assertEquals(accountList.get(i).getCurrency(), actualResponse.get(i).getCurrency());
            assertEquals(accountList.get(i).getBalance(), actualResponse.get(i).getBalance());
            assertEquals(accountList.get(i).getTreasury(), actualResponse.get(i).getTreasury());
        }
    }

    @Test
    @DisplayName("When findByTreasury not found")
    public void findByTreasury_NotFound() {
        assertThrows(AccountsListException.class, () -> accountsService.findAccountByTreasury("true"));
    }
}
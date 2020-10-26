package com.AccountService.App.AccountServiceApp.Controller;

import com.AccountService.App.AccountServiceApp.Exceptions.AccountResponseEntityExceptionHandler;
import com.AccountService.App.AccountServiceApp.Models.Account;
import com.AccountService.App.AccountServiceApp.Models.AccountsRepository;
import com.AccountService.App.AccountServiceApp.Models.Requests.CreateAccountRequest;
import com.AccountService.App.AccountServiceApp.Service.AccountsService;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Currency;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Given AccountController is called,")
public class AccountControllerIntegrationTest {
    private final Logger logger = LoggerFactory.getLogger(AccountControllerIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private AccountsController accountsController;

    private Account account;

    @BeforeEach
    public void initMockMvd() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountsController)
                .setControllerAdvice(new AccountResponseEntityExceptionHandler())
                .build();
        account = new Account(
                "Test01",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
    }

    @Test
    public void createAccount_OK() throws Exception {
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("name", "Test01");
        jsonRequest.addProperty("currency", "EUR");
        jsonRequest.addProperty("balance", 1000);
        jsonRequest.addProperty("treasury", false);
        logger.info(jsonRequest.toString());
        mockMvc.perform(post("/accounts")
                .contentType("application/json")
                .content(jsonRequest.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status").value("Account created"));
    }

    @Test
    public void createAccount_BAD_REQUEST() throws Exception {
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("name", "");
        jsonRequest.addProperty("currency", "EUR");
        jsonRequest.addProperty("balance", 1000);
        jsonRequest.addProperty("treasury", false);
        logger.info(jsonRequest.toString());
        mockMvc.perform(post("/accounts")
                .contentType("application/json")
                .content(jsonRequest.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("accerr"));
    }

    @Test
    public void listAllAccounts_FOUND() throws Exception {
        accountsRepository.save(account);
        Account account = new Account(
                "Test01",
                Currency.getInstance("EUR"),
                BigDecimal.valueOf(1000),
                true
        );
        accountsRepository.save(account);
        mockMvc.perform(get("/accounts")
                .contentType("application/json"))
                .andExpect(status().is3xxRedirection())
                .andExpect(jsonPath("$[0].name").value("Test01"));
        accountsRepository.delete(account);
    }

    @Test
    public void listAllAccounts_NOT_FOUND() throws Exception {
        mockMvc.perform(get("/accounts")
                .contentType("application/json"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("acclist"));
    }

    @Test
    public void findByName_FOUND() throws Exception {
        accountsRepository.save(account);
        mockMvc.perform(get("/accounts/findByName/" + account.getName())
                .contentType("application/json"))
                .andExpect(status().is3xxRedirection())
                .andExpect(jsonPath("$[0].name").value("Test01"));
        accountsRepository.delete(account);
    }

    @Test
    public void findByName_NOT_FOUND() throws Exception {
        accountsRepository.save(account);
        mockMvc.perform(get("/accounts/findByName/" + "TestX")
                .contentType("application/json"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("acclist"));
        accountsRepository.delete(account);
    }

    @Test
    public void findByCurrency_FOUND() throws Exception {
        accountsRepository.save(account);
        mockMvc.perform(get("/accounts/findByCurrency/" + account.getCurrency().getCurrencyCode())
                .contentType("application/json"))
                .andExpect(status().is3xxRedirection())
                .andExpect(jsonPath("$[0].currency").value("EUR"));
        accountsRepository.delete(account);
    }

    @Test
    public void findByCurrency_NOT_FOUND() throws Exception {
        accountsRepository.save(account);
        mockMvc.perform(get("/accounts/findByCurrency/" + "USD")
                .contentType("application/json"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("acclist"));
        accountsRepository.delete(account);
    }

    @Test
    public void findByTreasury_FOUND() throws Exception {
        accountsRepository.save(account);
        mockMvc.perform(get("/accounts/findByTreasury/" + account.getTreasury())
                .contentType("application/json"))
                .andExpect(status().is3xxRedirection())
                .andExpect(jsonPath("$[0].treasury").value("true"));
        accountsRepository.delete(account);
    }

    @Test
    public void findByTreasury_NOT_FOUND() throws Exception {
        accountsRepository.save(account);
        mockMvc.perform(get("/accounts/findByTreasury/" + "false")
                .contentType("application/json"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("acclist"));
        accountsRepository.delete(account);
    }
}

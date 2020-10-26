package com.AccountService.App.AccountServiceApp.Controller;

import com.AccountService.App.AccountServiceApp.Exceptions.AccountResponseEntityExceptionHandler;
import com.AccountService.App.AccountServiceApp.Models.AccountsRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Given AccountController is called,")
public class AccountControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountsRepository toDoRepository;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private AccountsController accountsController;
    private final Logger logger = LoggerFactory.getLogger(AccountControllerIntegrationTest.class);

    @BeforeEach
    public void initMockMvd() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountsController)
                .setControllerAdvice(new AccountResponseEntityExceptionHandler())
                .build();
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
}

package com.AccountService.App.AccountServiceApp.Models.Responses;

import com.AccountService.App.AccountServiceApp.Models.Requests.TransferMoneyRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class TransferMoneyResponse {
    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;
    private final String status;

    public TransferMoneyResponse(String status, TransferMoneyRequest request) {
        this.fromAccount = request.getFromAccount();
        this.toAccount = request.getToAccount();
        this.amount = request.getAmount();
        this.status = status;
    }
}

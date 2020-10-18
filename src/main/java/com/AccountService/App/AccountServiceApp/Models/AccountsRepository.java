package com.AccountService.App.AccountServiceApp.Models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountsRepository extends JpaRepository<Account, Long> {
    List<Account> findByName(String name);
}

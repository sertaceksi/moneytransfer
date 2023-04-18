package com.bank.moneytransaction.repository;

import com.bank.moneytransaction.entity.Account;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(value = {@QueryHint(name = "javax.persistence.lock.timeout", value = "-2")})
    Optional<Account> findByAccNo(Integer accountNo);
    List<Account> findByUserId(UUID userId);
}

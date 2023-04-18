package com.bank.moneytransaction.repository;

import com.bank.moneytransaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("SELECT t FROM Transaction t WHERE t.senderAccount.id = ?1 OR t.recipientAccount.id = ?1")
    List<Transaction> findByAccountId(UUID accountId);
}

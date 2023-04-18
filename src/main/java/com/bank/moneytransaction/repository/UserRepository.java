package com.bank.moneytransaction.repository;

import com.bank.moneytransaction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}

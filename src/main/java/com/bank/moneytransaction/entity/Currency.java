package com.bank.moneytransaction.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIncludeProperties(value = {"code"})
public class Currency {

    @Id
    @Column(nullable = false, updatable = false)
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private UUID id;

    @Column
    private String code;

    @Column
    private String name;

    @OneToMany(mappedBy = "currency", fetch = FetchType.LAZY)
    private Set<Account> currencyAccounts;

    @OneToMany(mappedBy = "currency")
    private Set<Transaction> currencyTransactions;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @JsonIgnore
    public Set<Account> getCurrencyAccounts() {
        return currencyAccounts;
    }

    public void setCurrencyAccounts(final Set<Account> currencyAccounts) {
        this.currencyAccounts = currencyAccounts;
    }

    @JsonIgnore
    public Set<Transaction> getCurrencyTransactions() {
        return currencyTransactions;
    }

    public void setCurrencyTransactions(final Set<Transaction> currencyTransactions) {
        this.currencyTransactions = currencyTransactions;
    }

}

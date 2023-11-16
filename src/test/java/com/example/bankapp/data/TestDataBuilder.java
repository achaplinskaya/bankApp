package com.example.bankapp.data;

import com.example.bankapp.model.entities.CustomerEntity;
import com.example.bankapp.model.entities.DepositEntity;
import com.example.bankapp.model.enums.DepositStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestDataBuilder {

    public DepositEntity createDeposit() {
        return DepositEntity.builder()
                .id(10L)
                .amount(BigDecimal.valueOf(1000000.00))
                .interestRate(BigDecimal.valueOf(19.00))
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().plusMonths(12))
                .customer(createCustomer())
                .status(DepositStatus.ACTIVE)
                .build();
    }

    public DepositEntity createOneMoreDeposit() {
        return DepositEntity.builder()
                .id(11L)
                .amount(BigDecimal.valueOf(1000.00))
                .interestRate(BigDecimal.valueOf(9.00))
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().plusMonths(12))
                .customer(createCustomer())
                .status(DepositStatus.ACTIVE)
                .build();
    }

    public CustomerEntity createCustomer() {
        return CustomerEntity.builder()
                .id(1L)
                .account("11111")
                .balance(BigDecimal.valueOf(100000.00))
                .build();
    }

    public List<DepositEntity> createDepositList() {
        List<DepositEntity> deposits = new ArrayList<>();
        deposits.add(createDeposit());
        deposits.add(createOneMoreDeposit());
        return deposits;
    }

    public DepositEntity createDepositClosed() {
        return DepositEntity.builder()
                .id(10L)
                .amount(BigDecimal.valueOf(1000000.00))
                .interestRate(BigDecimal.valueOf(19.00))
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().plusMonths(12))
                .customer(createCustomer())
                .status(DepositStatus.CLOSED)
                .build();
    }

}

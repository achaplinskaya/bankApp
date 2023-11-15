package com.example.bankapp.services.impl;

import com.example.bankapp.model.entities.CustomerEntity;
import com.example.bankapp.model.entities.DepositEntity;
import com.example.bankapp.model.entities.TransactionEntity;
import com.example.bankapp.model.enums.DepositStatus;
import com.example.bankapp.model.enums.TransactionStatus;
import com.example.bankapp.model.enums.TransactionType;
import com.example.bankapp.repositories.CustomerRepository;
import com.example.bankapp.repositories.DepositRepository;
import com.example.bankapp.repositories.TransactionRepository;
import com.example.bankapp.services.DepositService;
import com.example.bankapp.utils.TimeProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final DepositRepository depositRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final TimeProvider provider;

    @Override
    public BigDecimal calculateInterest(Long depositId) {
        DepositEntity deposit = findById(depositId);

        LocalDateTime currentDate = provider.getCurrentDateTime();

        BigDecimal interestRate = deposit.getInterestRate();
        BigDecimal depositAmount = deposit.getAmount();
        Duration depositDuration = Duration.between(deposit.getStartDate(), currentDate);
        long days = depositDuration.toDays();
        BigDecimal interest = switch (deposit.getInterestCapitalization()) {
            case MONTHLY -> calculateMonthlyInterest(interestRate, depositAmount, days);
            case QUARTERLY -> calculateQuarterlyInterest(interestRate, depositAmount, days);
            case END_OF_TERM -> calculateEndOfTermInterest(interestRate, depositAmount, days);
            default -> throw new IllegalArgumentException("Unsupported interest capitalization type");
        };

        return interest.setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    @Transactional
    public void transferInterestToCustomer(Long depositId) {
        DepositEntity deposit = findById(depositId);

        BigDecimal interest = calculateInterest(depositId);

        CustomerEntity customer = customerRepository.findById(deposit.getCustomer().getId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        customer.setBalance(customer.getBalance().add(interest));
        customerRepository.save(customer);

        createTransaction(deposit, TransactionType.INTEREST_PAYMENT, interest);
    }

    @Override
    @Transactional
    public void reinvestInterest(Long depositId) {
        DepositEntity deposit = findById(depositId);
        BigDecimal interest = calculateInterest(depositId);
        deposit.setAmount(deposit.getAmount().add(interest));
        depositRepository.save(deposit);
        createTransaction(deposit, TransactionType.REINVESTMENT, interest);
    }

    @Override
    public void updateDb() {
        LocalDateTime currentTime = provider.getCurrentDateTime();
        List<DepositEntity> depositEntityList = depositRepository.findAll();

        for (DepositEntity deposit: depositEntityList) {
            log.info("Status check");
            if (currentTime.isBefore(deposit.getStartDate()) || currentTime.isAfter(deposit.getEndDate())) {
                deposit.setStatus(DepositStatus.CLOSED);
                depositRepository.save(deposit);
            }
        }
        List<Long> ids = depositEntityList.stream().filter(d -> d.getStatus() == DepositStatus.ACTIVE)
                .map(d -> d.getId()).collect(Collectors.toList());
        for (Long id : ids) {
            log.info("ActionLog.processScheduledTasks.start: id: {}", id);
            reinvestInterest(id);
        }
    }

    @Override
    public void setCurrentDateTime(LocalDateTime dateTime) {
        provider.setCurrentDateTime(dateTime);
        log.info("Update DB");
        updateDb();
    }

    public static BigDecimal calculateMonthlyInterest(BigDecimal interestRate, BigDecimal depositAmount, long days) {
        return depositAmount.multiply(interestRate).multiply(BigDecimal.valueOf(days))
                .divide(BigDecimal.valueOf(36500), RoundingMode.HALF_EVEN);
    }

    public static BigDecimal calculateQuarterlyInterest(BigDecimal interestRate, BigDecimal depositAmount, long days) {
        return depositAmount.multiply(interestRate).multiply(BigDecimal.valueOf(days))
                .divide(BigDecimal.valueOf(365400), RoundingMode.HALF_EVEN);
    }

    public static BigDecimal calculateEndOfTermInterest(BigDecimal interestRate, BigDecimal depositAmount, long days) {
        return calculateMonthlyInterest(interestRate, depositAmount, days);
    }

    private void createTransaction(DepositEntity deposit, TransactionType transactionType, BigDecimal amount) {
        TransactionEntity transaction = TransactionEntity.builder()
                .type(transactionType)
                .amount(amount)
                .status(TransactionStatus.PENDING)
                .customer(deposit.getCustomer())
                .build();
        transactionRepository.save(transaction);
    }

    private DepositEntity findById(Long depositId) {
        return depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Deposit not found"));
    }

}

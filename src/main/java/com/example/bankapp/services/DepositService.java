package com.example.bankapp.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface DepositService {

    BigDecimal calculateInterest(Long id);

    void transferInterestToCustomer(Long id);

    void reinvestInterest(Long id);

    long calculatePeriod(LocalDateTime currentDate, LocalDateTime providedDate);
}

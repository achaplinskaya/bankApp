package com.example.bankapp;

import com.example.bankapp.model.entities.DepositEntity;
import com.example.bankapp.model.enums.InterestCapitalization;
import com.example.bankapp.repositories.DepositRepository;
import com.example.bankapp.services.DepositService;
import com.example.bankapp.utils.TimeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Autowired
    private DepositService depositService;
    @Mock
    private DepositRepository depositRepository;
    @Mock
    private TimeProvider provider;


    @Test
    void calculateInterestTest() {
        DepositEntity deposit = DepositEntity.builder()
                .id(10L)
                .amount(BigDecimal.valueOf(1000000.00))
                .interestRate(BigDecimal.valueOf(19.00))
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().plusMonths(12))
                .interestCapitalization(InterestCapitalization.MONTHLY)
                .build();
        when(depositRepository.findById(10L)).thenReturn(Optional.of(deposit));
        when(provider.getCurrentDateTime()).thenReturn(LocalDateTime.now());

        BigDecimal actual = depositService.calculateInterest(10L);
        assertEquals("", actual);
    }
}

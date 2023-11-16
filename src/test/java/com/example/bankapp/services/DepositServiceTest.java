package com.example.bankapp.services;

import com.example.bankapp.data.TestDataBuilder;
import com.example.bankapp.model.entities.CustomerEntity;
import com.example.bankapp.model.entities.DepositEntity;
import com.example.bankapp.model.entities.TransactionEntity;
import com.example.bankapp.repositories.CustomerRepository;
import com.example.bankapp.repositories.DepositRepository;
import com.example.bankapp.repositories.TransactionRepository;
import com.example.bankapp.services.impl.DepositServiceImpl;
import com.example.bankapp.utils.TimeProvider;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.bankapp.model.enums.InterestCapitalization.END_OF_TERM;
import static com.example.bankapp.model.enums.InterestCapitalization.MONTHLY;
import static com.example.bankapp.model.enums.InterestCapitalization.QUARTERLY;
import static com.example.bankapp.model.enums.InterestCapitalization.SOME_TYPE;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @InjectMocks
    private DepositServiceImpl depositService;
    @Mock
    private DepositRepository depositRepository;
    @Mock
    private TimeProvider provider;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private TransactionRepository transactionRepository;

    TestDataBuilder dataBuilder = new TestDataBuilder();

    @Test
    void calculateMonthlyInterestTest() {
        DepositEntity deposit = dataBuilder.createDeposit();
        deposit.setInterestCapitalization(MONTHLY);
        when(depositRepository.findById(10L)).thenReturn(Optional.of(deposit));
        when(provider.getCurrentDateTime()).thenReturn(LocalDateTime.now());

        BigDecimal actual = depositService.calculateInterest(10L);
        BigDecimal expected = BigDecimal.valueOf(15616.44);
        assertEquals(expected, actual);
    }

    @Test
    void calculateQuarterlyInterestTest() {
        DepositEntity deposit = dataBuilder.createDeposit();
        deposit.setInterestCapitalization(QUARTERLY);
        when(depositRepository.findById(10L)).thenReturn(Optional.of(deposit));
        when(provider.getCurrentDateTime()).thenReturn(LocalDateTime.now());

        BigDecimal actual = depositService.calculateInterest(10L);
        BigDecimal expected = BigDecimal.valueOf(1559.93);
        assertEquals(expected, actual);
    }

    @Test
    void calculateEndOfTermInterestTest() {
        DepositEntity deposit = dataBuilder.createDeposit();
        deposit.setInterestCapitalization(END_OF_TERM);
        when(depositRepository.findById(10L)).thenReturn(Optional.of(deposit));
        when(provider.getCurrentDateTime()).thenReturn(LocalDateTime.now());

        BigDecimal actual = depositService.calculateInterest(10L);
        BigDecimal expected = BigDecimal.valueOf(15616.44);
        assertEquals(expected, actual);
    }

    @Test
    void calculateInterestUnsupportedInterestCapitalizationTypeTest() {
        DepositEntity deposit = dataBuilder.createDeposit();
        deposit.setInterestCapitalization(SOME_TYPE);
        when(depositRepository.findById(10L)).thenReturn(Optional.of(deposit));
        when(provider.getCurrentDateTime()).thenReturn(LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> depositService.calculateInterest(10L));
    }

    @Test
    void transferInterestToCustomerTest() {
        DepositEntity deposit = dataBuilder.createDeposit();
        CustomerEntity customer = dataBuilder.createCustomer();
        deposit.setInterestCapitalization(QUARTERLY);
        when(depositRepository.findById(10L)).thenReturn(Optional.of(deposit));
        when(provider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        depositService.transferInterestToCustomer(10L);

        verify(depositRepository, times(2)).findById(any());
        verify(customerRepository, times(1)).findById(customer.getId());
        verify(customerRepository, times(1)).save(customer);
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));

        assertEquals(BigDecimal.valueOf(101559.93), customer.getBalance());
    }

    @Test
    void transferInterestToCustomerWhoIsNotExistTest() {
        DepositEntity deposit = dataBuilder.createDeposit();
        deposit.setInterestCapitalization(QUARTERLY);
        when(depositRepository.findById(10L)).thenReturn(Optional.of(deposit));
        when(provider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(customerRepository.findById(any())).thenReturn(empty());

        assertThrows(EntityNotFoundException.class, () -> depositService.transferInterestToCustomer(deposit.getId()));
    }

    @Test
    void reinvestInterestTest() {
        DepositEntity deposit = dataBuilder.createDeposit();
        deposit.setInterestCapitalization(QUARTERLY);
        when(depositRepository.findById(10L)).thenReturn(Optional.of(deposit));
        when(provider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(depositRepository.save(any())).thenReturn(deposit);

        depositService.reinvestInterest(deposit.getId());

        verify(depositRepository, times(2)).findById(any());
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));

        assertEquals(BigDecimal.valueOf(1001559.93), deposit.getAmount());
    }

    @Test
    void reinvestInterestFailureTest() {
        DepositEntity deposit = dataBuilder.createDeposit();
        when(depositRepository.findById(10L)).thenReturn(empty());

        assertThrows(EntityNotFoundException.class, () -> depositService.reinvestInterest(deposit.getId()));
    }
}

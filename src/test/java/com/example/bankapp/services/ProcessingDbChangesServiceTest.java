package com.example.bankapp.services;

import com.example.bankapp.data.TestDataBuilder;
import com.example.bankapp.model.entities.DepositEntity;
import com.example.bankapp.repositories.DepositRepository;
import com.example.bankapp.services.impl.ProcessingDbChangesServiceImpl;
import com.example.bankapp.utils.TimeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.bankapp.model.enums.InterestCapitalization.QUARTERLY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessingDbChangesServiceTest {

    @InjectMocks
    private ProcessingDbChangesServiceImpl processingDbChangesService;
    @Mock
    private DepositRepository depositRepository;
    @Mock
    private TimeProvider provider;

    @Mock
    private DepositService depositService;

    TestDataBuilder dataBuilder = new TestDataBuilder();


    @Test
    void updateDBFailureWithDepositStatusCloseTest() {
        List<DepositEntity> deposits = dataBuilder.createDepositList();
        DepositEntity deposit = dataBuilder.createDeposit();
        DepositEntity depositClosed = dataBuilder.createDepositClosed();
        deposit.setInterestCapitalization(QUARTERLY);

        when(provider.getCurrentDateTime()).thenReturn(LocalDateTime.now().minusDays(60));
        when(depositRepository.findAll()).thenReturn(deposits);
        when(depositService.calculatePeriod(any(),any())).thenReturn(2L);

        processingDbChangesService.updateDb();

        assertEquals("CLOSED", depositClosed.getStatus().name());
    }

    @Test
    void updateDBTest() {
        List<DepositEntity> deposits = dataBuilder.createDepositList();
        DepositEntity deposit = dataBuilder.createDeposit();
        DepositEntity depositClosed = dataBuilder.createDepositClosed();
        deposit.setInterestCapitalization(QUARTERLY);

        when(provider.getCurrentDateTime()).thenReturn(LocalDateTime.now());
        when(depositRepository.findAll()).thenReturn(deposits);
        when(depositService.calculatePeriod(any(),any())).thenReturn(2L);

        processingDbChangesService.updateDb();

        verify(depositRepository, times(1)).findAll();
        verify(provider, times(1)).getCurrentDateTime();
    }

    @Test
    void setCurrentDateTimeTest() {

        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
        when(provider.getCurrentDateTime()).thenReturn(dateTime);
        processingDbChangesService.setCurrentDateTime(dateTime);

        verify(provider, times(1)).setCurrentDateTime(dateTime);
    }

    @Test
    void setCurrentDateTimeFailureTest() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(3);

        assertThrows(IllegalArgumentException.class, () -> processingDbChangesService.setCurrentDateTime(dateTime));
    }
}

package com.example.bankapp.services.impl;

import com.example.bankapp.model.entities.DepositEntity;
import com.example.bankapp.model.enums.DepositStatus;
import com.example.bankapp.repositories.DepositRepository;
import com.example.bankapp.services.DepositService;
import com.example.bankapp.services.ProcessingDbChangesService;
import com.example.bankapp.utils.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingDbChangesServiceImpl implements ProcessingDbChangesService {

    private final TimeProvider provider;
    private final DepositService depositService;
    private final DepositRepository depositRepository;

    @Override
    public void updateDb() {
        LocalDateTime providedTime = provider.getCurrentDateTime();
        long days = depositService.calculatePeriod(LocalDateTime.now(), providedTime);
        List<DepositEntity> depositEntityList = depositRepository.findAll();

        for (DepositEntity deposit : depositEntityList) {
            log.info("Status check");
            if (providedTime.isBefore(deposit.getStartDate()) || providedTime.isAfter(deposit.getEndDate())) {
                deposit.setStatus(DepositStatus.CLOSED);
                depositRepository.save(deposit);
            }
        }
        List<Long> ids = depositEntityList.stream().filter(d -> d.getStatus() == DepositStatus.ACTIVE)
                .map(DepositEntity::getId).toList();
        for (Long id : ids) {
            if (days == 2) {
                depositService.reinvestInterest(id);
            } else {
                log.info("ActionLog.processScheduledTasks.start: id: {}", id);
                for (int i = 0; i < days; i++) {
                    provider.setCurrentDateTime(LocalDateTime.now().plusDays(i));
                    depositService.reinvestInterest(id);
                }
            }
        }
    }

    @Override
    public void setCurrentDateTime(LocalDateTime dateTime) {

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Date must not be less then current data");
        }
        provider.setCurrentDateTime(dateTime);
        updateDb();
    }
}

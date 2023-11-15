package com.example.bankapp.services.impl;

import com.example.bankapp.services.DepositService;
import com.example.bankapp.services.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {
    private final DepositService depositService;

    @Override
    @Scheduled(fixedRate = 60000)
    public void processScheduledTasks() {
        depositService.updateDb();
    }

}

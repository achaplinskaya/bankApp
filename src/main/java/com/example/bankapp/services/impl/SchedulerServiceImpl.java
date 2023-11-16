package com.example.bankapp.services.impl;

import com.example.bankapp.services.ProcessingDbChangesService;
import com.example.bankapp.services.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final ProcessingDbChangesService processingDbChangesService;

    @Override
    @Scheduled(fixedRate = 300000) // once a day
    public void processScheduledTasks() {
        processingDbChangesService.updateDb();
    }

}

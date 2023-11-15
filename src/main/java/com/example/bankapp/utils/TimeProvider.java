package com.example.bankapp.utils;

import com.example.bankapp.services.DepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeProvider {

    private LocalDateTime currentDateTime;

    public LocalDateTime getCurrentDateTime() {
        if (currentDateTime == null) {
            return LocalDateTime.now();
        }
        return currentDateTime;
    }
    public void setCurrentDateTime(LocalDateTime dateTime) {
        this.currentDateTime = dateTime;
    }
}

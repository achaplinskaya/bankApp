package com.example.bankapp.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class TimeProvider {

    private LocalDateTime currentDate;

    public LocalDateTime getCurrentDateTime() {
        if (currentDate == null) {
            return LocalDateTime.now();
        }
        return currentDate;
    }

    public void setCurrentDateTime(LocalDateTime dateTime) {
        this.currentDate = dateTime;
    }
}

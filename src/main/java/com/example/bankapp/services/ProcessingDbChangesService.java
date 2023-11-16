package com.example.bankapp.services;

import java.time.LocalDateTime;

public interface ProcessingDbChangesService {

    void updateDb();
    void setCurrentDateTime(LocalDateTime dateTime);
}

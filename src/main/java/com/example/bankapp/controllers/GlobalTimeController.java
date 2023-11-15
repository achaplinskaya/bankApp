package com.example.bankapp.controllers;

import com.example.bankapp.services.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/global-time")
@Tag(name = "GlobalTime")
public class GlobalTimeController {

    private final DepositService depositService;


    @PostMapping("/set")
    @Operation(summary = "Set global time")
    public ResponseEntity<String> setGlobalTime(@RequestBody LocalDateTime newGlobalTime) {
        depositService.setCurrentDateTime(newGlobalTime);
        return ResponseEntity.ok("Global time set to: " + newGlobalTime);
    }
}

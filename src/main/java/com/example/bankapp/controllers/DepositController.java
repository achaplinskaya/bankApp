package com.example.bankapp.controllers;

import com.example.bankapp.services.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api/deposits")
@Tag(name = "Deposit")
public class DepositController {

    private final DepositService depositService;

    @GetMapping("/{depositId}/calculate-interest")
    @Operation(summary = "Calculate an interest")
    public ResponseEntity<BigDecimal> calculateInterest(@PathVariable Long depositId){
       return ResponseEntity.ok(depositService.calculateInterest(depositId));
    }


    @PostMapping("/transfer-interest")
    @Operation(summary = "Transfer an interest to the customer account")
    public void transferInterestToCustomer(@RequestParam Long depositId) {
        depositService.transferInterestToCustomer(depositId);
    }

    @PostMapping("/reinvest-interest")
    @Operation(summary = "Reinvest an interest")
    public void reinvestInterest(@RequestParam Long depositId) {
        depositService.reinvestInterest(depositId);
    }
}

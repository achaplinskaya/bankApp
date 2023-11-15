package com.example.bankapp.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepositDto {

    private Long id;
    private String amount;
    private String interestRate;
    private String interestCapitalization;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long customerId;
}

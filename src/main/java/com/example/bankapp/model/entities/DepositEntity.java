package com.example.bankapp.model.entities;

import com.example.bankapp.model.enums.DepositStatus;
import com.example.bankapp.model.enums.InterestCapitalization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "deposits")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositEntity extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "interest_value", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "interest_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InterestCapitalization interestCapitalization;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DepositStatus status;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

}

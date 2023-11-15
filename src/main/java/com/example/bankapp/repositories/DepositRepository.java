package com.example.bankapp.repositories;

import com.example.bankapp.model.entities.DepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<DepositEntity, Long> {
}

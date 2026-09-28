package com.contractwatch.contractwatch.repository;

import com.contractwatch.contractwatch.model.Category;
import com.contractwatch.contractwatch.model.Contract;
import com.contractwatch.contractwatch.model.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    
    List<Contract> findByExpirationDateBetweenAndStatusNot(LocalDate startDate, LocalDate endDate, ContractStatus status);

    List<Contract> findByCategory(Category category);
}
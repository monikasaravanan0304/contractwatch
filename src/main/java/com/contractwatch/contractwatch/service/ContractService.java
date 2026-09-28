package com.contractwatch.contractwatch.service;

import com.contractwatch.contractwatch.dto.ContractRequestDTO;
import com.contractwatch.contractwatch.model.*;
import com.contractwatch.contractwatch.repository.ContractRepository;
import com.contractwatch.contractwatch.repository.RenewalDecisionRepository;
import com.contractwatch.contractwatch.repository.VendorRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final VendorRepository vendorRepository;
    private final RenewalDecisionRepository renewalDecisionRepository;

    public ContractService(ContractRepository contractRepository,
                           VendorRepository vendorRepository,
                           RenewalDecisionRepository renewalDecisionRepository) {
        this.contractRepository = contractRepository;
        this.vendorRepository = vendorRepository;
        this.renewalDecisionRepository = renewalDecisionRepository;
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public Contract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with id: " + id));
    }

    @Transactional
    public Contract createContract(ContractRequestDTO dto) {
        Contract contract = new Contract();
        mapDtoToEntity(dto, contract);
        
        // Evaluate notice window before initial save
        evaluateNoticeWindow(contract);

        return contractRepository.save(contract);
    }

    // Deletion method referenced by ContractController
    @Transactional
    public void deleteContract(Long id) {
        if (!contractRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Contract not found with id: " + id);
        }
        contractRepository.deleteById(id);
    }

    // Business Rule 1: Single Contract Notice Window Evaluation
    public void evaluateNoticeWindow(Contract contract) {
        if (contract.getStatus() == ContractStatus.CANCELLED || contract.getStatus() == ContractStatus.EXPIRED) {
            return; // Skip terminated or expired contracts
        }

        LocalDate today = LocalDate.now();
        int reminderDays = contract.getReminderDaysBefore() != null ? contract.getReminderDaysBefore() : 30;
        
        if (contract.getExpirationDate() != null) {
            LocalDate noticeBoundaryDate = contract.getExpirationDate().minusDays(reminderDays);

            // Rule: If today >= notice boundary date, flag as EXPIRING_SOON
            if (!today.isBefore(noticeBoundaryDate)) {
                contract.setStatus(ContractStatus.EXPIRING_SOON);
                System.out.println("[NOTIFICATION LOG] Contract #" + contract.getId() + " (" + contract.getTitle() + 
                                   ") auto-flagged for renewal review!");
            }
        }
    }

    /**
     * Business Rule 1 (Automated Background Task):
     * Scans active contracts automatically and flags those entering their notice period.
     * Scheduled to run daily at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkAndFlagAllExpiringContracts() {
        List<Contract> activeContracts = contractRepository.findAll().stream()
                .filter(c -> c.getStatus() == ContractStatus.ACTIVE)
                .toList();

        for (Contract contract : activeContracts) {
            evaluateNoticeWindow(contract);
            contractRepository.save(contract);
        }
    }

    // Business Rule 2: View active contracts expiring in the next N days
    public List<Contract> getExpiringContracts(int days) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(days);
        
        return contractRepository.findByExpirationDateBetweenAndStatusNot(today, targetDate, ContractStatus.CANCELLED);
    }

    // Business Rule 3: Log a Renewal Decision (RENEWED or TERMINATED)
    @Transactional
    public Contract logRenewalDecision(Long contractId, RenewalDecision.DecisionType decisionType, LocalDate newEndDate, String notes) {
        Contract contract = getContractById(contractId);

        RenewalDecision decision = contract.getRenewalDecision();
        if (decision == null) {
            decision = new RenewalDecision();
            decision.setContract(contract);
        }

        decision.setDecision(decisionType);
        decision.setDecisionDate(LocalDate.now());
        decision.setNotes(notes);

        if (decisionType == RenewalDecision.DecisionType.TERMINATED) {
            contract.setStatus(ContractStatus.CANCELLED);
            decision.setNewEndDate(null);
        } else if (decisionType == RenewalDecision.DecisionType.RENEWED) {
            if (newEndDate == null || !newEndDate.isAfter(contract.getExpirationDate())) {
                throw new IllegalArgumentException("Renewed contracts must have a valid future end date.");
            }
            contract.setExpirationDate(newEndDate);
            contract.setStatus(ContractStatus.ACTIVE);
            decision.setNewEndDate(newEndDate);
        }

        contract.setRenewalDecision(decision);
        return contractRepository.save(contract);
    }

    // Feature 5: Attach document reference URL
    @Transactional
    public Contract attachDocumentUrl(Long contractId, String documentUrl) {
        Contract contract = getContractById(contractId);
        contract.setDocumentUrl(documentUrl);
        return contractRepository.save(contract);
    }

    private void mapDtoToEntity(ContractRequestDTO dto, Contract contract) {
        contract.setTitle(dto.getTitle());
        contract.setDescription(dto.getDescription());
        
        // Find or create Vendor
        if (dto.getVendor() != null && !dto.getVendor().isBlank()) {
            Vendor vendor = vendorRepository.findByName(dto.getVendor())
                    .orElseGet(() -> vendorRepository.save(new Vendor(dto.getVendor(), null, null)));
            contract.setVendor(vendor);
        }

        contract.setStartDate(dto.getStartDate());
        contract.setExpirationDate(dto.getExpirationDate());
        contract.setCost(dto.getCost());
        contract.setAutoRenew(dto.getAutoRenew() != null ? dto.getAutoRenew() : false);
        contract.setReminderDaysBefore(dto.getReminderDaysBefore() != null ? dto.getReminderDaysBefore() : 30);
        contract.setCategory(dto.getCategory());
        
        // Default status set to ACTIVE if not supplied by DTO
        contract.setStatus(dto.getStatus() != null ? dto.getStatus() : ContractStatus.ACTIVE);
    }
}
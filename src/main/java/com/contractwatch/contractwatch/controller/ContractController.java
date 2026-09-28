package com.contractwatch.contractwatch.controller;

import com.contractwatch.contractwatch.dto.ContractRequestDTO;
import com.contractwatch.contractwatch.model.Contract;
import com.contractwatch.contractwatch.model.RenewalDecision;
import com.contractwatch.contractwatch.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService service;

    public ContractController(ContractService service) {
        this.service = service;
    }

    @GetMapping
    public List<Contract> getAllContracts() {
        return service.getAllContracts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContractById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getContractById(id));
    }

    @PostMapping
    public ResponseEntity<Contract> createContract(@Valid @RequestBody ContractRequestDTO dto) {
        return new ResponseEntity<>(service.createContract(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        service.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expiring")
    public List<Contract> getExpiringContracts(@RequestParam(defaultValue = "30") int days) {
        return service.getExpiringContracts(days);
    }

    @PostMapping("/{id}/decision")
    public ResponseEntity<Contract> logDecision(
            @PathVariable Long id,
            @RequestParam RenewalDecision.DecisionType decision,
            @RequestParam(required = false) String newEndDate,
            @RequestParam(required = false) String notes) {
        
        LocalDate parsedNewEndDate = newEndDate != null ? LocalDate.parse(newEndDate) : null;
        return ResponseEntity.ok(service.logRenewalDecision(id, decision, parsedNewEndDate, notes));
    }

    @PatchMapping("/{id}/document")
    public ResponseEntity<Contract> attachDocument(
            @PathVariable Long id,
            @RequestParam String documentUrl) {
        return ResponseEntity.ok(service.attachDocumentUrl(id, documentUrl));
    }
}
package com.contractwatch.contractwatch.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "renewal_decisions")
public class RenewalDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecisionType decision; // RENEWED or TERMINATED

    private LocalDate decisionDate;
    private LocalDate newEndDate;
    private String notes;

    public enum DecisionType {
        RENEWED,
        TERMINATED
    }

    public RenewalDecision() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }

    public DecisionType getDecision() { return decision; }
    public void setDecision(DecisionType decision) { this.decision = decision; }

    public LocalDate getDecisionDate() { return decisionDate; }
    public void setDecisionDate(LocalDate decisionDate) { this.decisionDate = decisionDate; }

    public LocalDate getNewEndDate() { return newEndDate; }
    public void setNewEndDate(LocalDate newEndDate) { this.newEndDate = newEndDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
package com.contractwatch.contractwatch.dto;

import com.contractwatch.contractwatch.model.Category;
import com.contractwatch.contractwatch.model.ContractStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContractRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Vendor name is required")
    private String vendor;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Expiration date is required")
    @FutureOrPresent(message = "Expiration date must be today or in the future")
    private LocalDate expirationDate;

    private BigDecimal cost;

    private Boolean autoRenew;

    private Integer reminderDaysBefore;

    @NotNull(message = "Category is required")
    private Category category;

    private ContractStatus status;

    public ContractRequestDTO() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public Boolean getAutoRenew() { return autoRenew; }
    public void setAutoRenew(Boolean autoRenew) { this.autoRenew = autoRenew; }

    public Integer getReminderDaysBefore() { return reminderDaysBefore; }
    public void setReminderDaysBefore(Integer reminderDaysBefore) { this.reminderDaysBefore = reminderDaysBefore; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) { this.status = status; }
}
package com.bilgeadam.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
public class Expense extends BaseEntity{
    @Id
    private String id;
    private String requestUserId; //employee
    private String responseUserId; //manager
    private LocalDate requestDate;
    private LocalDate responseDate;
    private String description;
    private Double expenseAmount;
    private String expenseType;
    private String unitOfCurrency;
    private String url;
}

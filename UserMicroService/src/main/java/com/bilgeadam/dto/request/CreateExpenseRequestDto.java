package com.bilgeadam.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateExpenseRequestDto {
    private String token;
    private LocalDate requestDate;
    private String description;
    private Double expenseAmount;
    private String expenseType;
    private String unitOfCurrency;
}

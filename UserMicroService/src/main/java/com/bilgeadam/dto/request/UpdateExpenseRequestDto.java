package com.bilgeadam.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateExpenseRequestDto {
    private String token;
    private String id;
    private LocalDate requestDate;
    private String description;
    private Double expenseAmount;
    private String expenseType;
    private String unitOfCurrency;
}

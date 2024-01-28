package com.bilgeadam.dto.response;

import com.bilgeadam.utility.enums.EState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExpensesListResponseDtoForRequestUser {
    private String responseUserId;
    private LocalDate requestDate;
    private LocalDate responseDate;
    private String description;
    private Double expenseAmount;
    private String expenseType;
    private String unitOfCurrency;
    private String url;
    private EState state;

}

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
public class UpdateAdvanceRequestDto {
    private String token;
    private String id;
    private Long advanceAmount;
    private String unitOfCurrency;
    private String description;
    private String advanceType;
    private LocalDate requestDate;
}

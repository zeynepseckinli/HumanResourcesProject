package com.bilgeadam.dto.request;

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
public class CreateAdvanceRequestDto {

    private String token;
    private String responseUserId;
    private Long advanceAmount;
    private String unitOfCurrency;
    private String description;
    private String advanceType;
    private LocalDate requestDate;

}

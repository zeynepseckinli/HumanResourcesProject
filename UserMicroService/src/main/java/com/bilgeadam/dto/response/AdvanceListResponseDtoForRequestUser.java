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
public class AdvanceListResponseDtoForRequestUser {
    private String responseUserId;
    private Long advanceAmount;
    private String unitOfCurrency;
    private String description;
    private String advanceType;
    private LocalDate requestDate;
    private LocalDate responseDate;
    private EState state;
}

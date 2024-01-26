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
public class AdvanceListResponseDtoForResponseUser {
    private Long advanceAmount;
    private String unitOfCurrency;
    private String description;
    private String advanceType;
    private LocalDate requestDate;
    private LocalDate responseDate;
    private EState state;
    private String requestUserId;
    private String name;
    private String secondName;
    private String surname;
    private String secondSurname;
    private String email;
    private Long salary;



}

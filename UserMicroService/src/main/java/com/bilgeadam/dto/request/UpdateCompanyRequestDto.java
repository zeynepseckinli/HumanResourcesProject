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
public class UpdateCompanyRequestDto {

    private String id;
    private String token;
    private String name;
    private String title;
    private String taxNumber;
    private String taxAdministration;
    private String address;
    private String phone;
    private String email;
    private int numberOfEmployees;
    private LocalDate yearOfEstablishment;
    private LocalDate contractStartDare;
    private LocalDate contractEndDate;
    private EState state;


}
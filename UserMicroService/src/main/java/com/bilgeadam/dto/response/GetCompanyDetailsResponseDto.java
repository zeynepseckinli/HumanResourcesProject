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
public class GetCompanyDetailsResponseDto {

    private String id;
    private String name;
    private String title;
    private String taxNumber;
    private String address;
    private String phone;
    private String email;
    private int numberOfEmployees;
    private LocalDate yearOfEstablishment;
    private EState state;

}

package com.bilgeadam.dto.request;

import com.bilgeadam.utility.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateUserRequestDto {
    private ERole role;
    private String name;
    private String secondName;
    private String surname;
    private String secondSurname;
    private String personalEmail;
    private String identityNumber;
    private String birthPlace;
    private String occupation;
    private String department;
    private String companyId;
    private String managerId;
    private String address;
    private String phone;
    private Long salary;
    private LocalDate birthDate;
    private LocalDate jobStart;

}

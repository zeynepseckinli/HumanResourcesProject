package com.bilgeadam.repository.entity;

import com.bilgeadam.utility.enums.ERole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class UserProfile extends BaseEntity {

    @Id
    private String id;
    private Long authId;
    @NotNull
    private String managerId;
    private String name;
    @Builder.Default
    private String secondName = "";
    private String surname;
    @Builder.Default
    private String secondSurname = "";
    private String email;
    private String activationCode;
    private String personalEmail;
    private String identityNumber;
    private String birthPlace;
    private String occupation;
    private String department;
    private String companyId;
    private String address;
    private String phone;
    @Builder.Default
    private Long salary = 0L;
    private ERole role;
    private LocalDate birthDate;
    private LocalDate jobStart;
    private LocalDate jobEnd;
    private String avatar;


}

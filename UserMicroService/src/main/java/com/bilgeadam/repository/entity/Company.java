package com.bilgeadam.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
public class Company extends BaseEntity {

    @Id
    private String id;
    private String companyName;
    private String title;
    private String address;
    private String phoneNumber;
    private String email;
    private int numberOfEmployees;
    private LocalDate yearOfEstablishment;
}

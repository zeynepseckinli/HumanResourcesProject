package com.bilgeadam.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
public class Company extends BaseEntity {

    @Id
    private String id;
    private String name;
    private String taxNumber;
    private String title;
    private String address;
    private String phone;
    private String email;
    private int numberOfEmployees;
    private LocalDate yearOfEstablishment;
}
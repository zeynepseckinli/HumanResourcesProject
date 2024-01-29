package com.bilgeadam.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
public class Permission extends BaseEntity {

    @Id
    private String id;
    private String requestUserId; //employee
    private String responseUserId; //manager
    private String permissionType;
    private LocalDate requestDate;
    private LocalDate responseDate;
    private LocalDate permissionStart;
    private LocalDate permissionEnd;
    private int permissionDuration;

}

package com.bilgeadam.repository.entity;

import com.bilgeadam.utility.enums.State;
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
public class Permission extends BaseEntity {

    @Id
    private String id;
    private String requestUserId; //employee
    private String responseUserId; //manager
    private String permissionType;
    private LocalDate dateOfRequest;
    private LocalDate dateOfResponse;
    private LocalDate startOfPermission;
    private LocalDate endOfPermission;
    private int permissionDuration;
    private State permissionState;


}

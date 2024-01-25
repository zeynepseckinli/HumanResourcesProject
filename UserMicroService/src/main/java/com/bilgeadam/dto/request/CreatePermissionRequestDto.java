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
public class CreatePermissionRequestDto {

    private String token;
    private String requestUserId;
    private String permissionType;
    private LocalDate dateOfRequest;
    private LocalDate startOfPermission;
    private LocalDate endOfPermission;
    private int permissionDuration;
    private EState permissionState;

}


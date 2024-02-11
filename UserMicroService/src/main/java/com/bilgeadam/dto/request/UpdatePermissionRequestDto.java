package com.bilgeadam.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePermissionRequestDto {
    private String token;
    private String id;
    private String permissionType;
    private LocalDate permissionStart;
    private LocalDate permissionEnd;
    private int permissionDuration;
}

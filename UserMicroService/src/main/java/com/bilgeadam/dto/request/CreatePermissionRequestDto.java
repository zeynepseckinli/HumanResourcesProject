package com.bilgeadam.dto.request;

import com.bilgeadam.utility.enums.EState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatePermissionRequestDto {

    private String token;
    private String permissionType;
    private LocalDate permissionStart;
    private LocalDate permissionEnd;
    private int permissionDuration;

}


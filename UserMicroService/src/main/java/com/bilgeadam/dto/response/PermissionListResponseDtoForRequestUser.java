package com.bilgeadam.dto.response;

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
public class PermissionListResponseDtoForRequestUser {
    private String responseUserId;
    private String permissionType;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate permissionStart;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate permissionEnd;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate requestDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate responseDate;
    private int permissionDuration;
    private EState state;
}

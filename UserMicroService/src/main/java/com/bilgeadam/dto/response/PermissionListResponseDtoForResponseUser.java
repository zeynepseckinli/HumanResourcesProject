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
public class PermissionListResponseDtoForResponseUser {
    private String permissionType;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate permissionStart;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate permissionEnd;
    private int permissionDuration;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate requestDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate responseDate;
    private EState state;
    private String requestUserId;
    private String name;
    private String secondName;
    private String surname;
    private String secondSurname;
    private String email;
}

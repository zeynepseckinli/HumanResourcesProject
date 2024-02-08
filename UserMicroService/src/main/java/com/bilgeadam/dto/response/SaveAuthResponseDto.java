package com.bilgeadam.dto.response;

import com.bilgeadam.utility.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaveAuthResponseDto {
    boolean isSave;
    String message;
    Long authId;
    ERole role;
}

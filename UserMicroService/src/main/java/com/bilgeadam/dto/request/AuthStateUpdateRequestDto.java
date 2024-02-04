package com.bilgeadam.dto.request;

import com.bilgeadam.utility.enums.EState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthStateUpdateRequestDto {
    private String token;
    private Long authId;
    private EState selectedState;
}

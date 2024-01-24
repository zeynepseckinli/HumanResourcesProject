package com.bilgeadam.dto.response;

import com.bilgeadam.utility.enums.ERole;
import com.bilgeadam.utility.enums.EState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponseDto {
    String token;
    ERole role;
    Long authId;
    EState state;

}

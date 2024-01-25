package com.bilgeadam.dto.response;

import com.bilgeadam.repository.entity.Auth;
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
}

package com.bilgeadam.dto.request;

import com.bilgeadam.utility.enums.ERole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveAuthRequestDto {

    @Email
    private String email;
    @NotBlank
    @Size(min =8, max=32, message = "Sifre minimum 8 maksimum 32 karakterden olusmalidir...")
    private String password;
    private ERole role;


}

package com.bilgeadam.controller;

import com.bilgeadam.dto.request.*;

import com.bilgeadam.dto.response.LoginResponseDto;

import com.bilgeadam.dto.response.SaveAuthResponseDto;
import com.bilgeadam.repository.entity.Auth;

import com.bilgeadam.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.bilgeadam.constants.RestApiUrls.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH)
@CrossOrigin("/api/v1/user")
public class AuthController {

    private final AuthService authService;

    @Hidden
    @GetMapping("/get-message")
    public String getMessage(){
        return "This is Auth Service";
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto){
        return ResponseEntity.ok(authService.login(dto));
    }

    @Hidden
    @PostMapping("/save")
    public ResponseEntity<SaveAuthResponseDto>  save (@RequestBody @Valid SaveAuthRequestDto dto){
        Auth auth= authService.save(dto);
        return ResponseEntity.ok(SaveAuthResponseDto.builder()
                        .isSave(true)
                        .message("Saved successfully")
                        .authId(auth.getId())
                .build());
    }
//    @PostMapping("/findauthidbyemail")
//    @CrossOrigin("*")
//    public ResponseEntity<Long> findAuthIdByEmail(@RequestBody String email){
//        return ResponseEntity.ok(authService.findAuthIdByEmail(email));
//    }
    @Hidden
    @PutMapping("/update")
    public ResponseEntity<Boolean> updateAuth(@RequestBody AuthUpdateRequestDto dto){
        return ResponseEntity.ok(authService.updateAuth(dto));
    }

    @Hidden
    @PostMapping("/update-role")
    public ResponseEntity<Boolean> updateRole(@RequestBody AuthRoleUpdateRequestDto dto){
        return ResponseEntity.ok(authService.updateRole(dto));
    }


    @PostMapping("/update-state")
    public ResponseEntity<Boolean> updateAuthState(@RequestBody AuthStateUpdateRequestDto dto){
        return ResponseEntity.ok(authService.updateAuthState(dto));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Boolean> changePassword(@RequestBody @Valid ChangePasswordDto dto){
        return ResponseEntity.ok(authService.changePassword(dto));
    }



}

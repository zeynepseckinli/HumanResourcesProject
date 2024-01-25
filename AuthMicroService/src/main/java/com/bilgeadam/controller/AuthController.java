package com.bilgeadam.controller;

import com.bilgeadam.dto.request.AuthUpdateRequestDto;
import com.bilgeadam.dto.request.LoginRequestDto;

import com.bilgeadam.dto.response.LoginResponseDto;

import com.bilgeadam.dto.request.SaveAuthRequestDto;
import com.bilgeadam.dto.response.SaveAuthResponseDto;
import com.bilgeadam.repository.entity.Auth;

import com.bilgeadam.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.bilgeadam.constants.RestApiUrls.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH)
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/getmessage")
    public String getMessage(){
        return "This is Auth Service";
    }

    @PostMapping(LOGIN)
    @CrossOrigin("*")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto){
        return ResponseEntity.ok(authService.login(dto));
    }

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
    @PutMapping("/update")
    public ResponseEntity<Boolean> updateAuth(@RequestBody AuthUpdateRequestDto dto){
        return ResponseEntity.ok(authService.updateAuth(dto));
    }
}

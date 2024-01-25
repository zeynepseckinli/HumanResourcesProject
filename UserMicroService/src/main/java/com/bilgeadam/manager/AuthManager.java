package com.bilgeadam.manager;

import com.bilgeadam.dto.request.AuthUpdateRequestDto;
import com.bilgeadam.dto.request.SaveAuthRequestDto;

import com.bilgeadam.dto.response.SaveAuthResponseDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "http://localhost:9090/api/v1/auth",name = "authManager")
public interface AuthManager {

    @GetMapping("/getmessage")
    public String getMessage();

    @PostMapping("/save")
    public ResponseEntity<SaveAuthResponseDto>  save (@RequestBody @Valid SaveAuthRequestDto dto);

//    @GetMapping("/findauthidbyemail")
//    public ResponseEntity<Long> findAuthIdByEmail(@RequestBody String email);

    @PutMapping("/update")
    public ResponseEntity<Boolean> updateAuth(@RequestBody AuthUpdateRequestDto dto);
}
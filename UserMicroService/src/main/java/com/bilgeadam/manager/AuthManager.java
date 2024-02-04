package com.bilgeadam.manager;

import com.bilgeadam.dto.request.AuthRoleUpdateRequestDto;
import com.bilgeadam.dto.request.AuthStateUpdateRequestDto;
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

    @GetMapping("/get-message")
    public String getMessage();

    @PostMapping("/save")
    public ResponseEntity<SaveAuthResponseDto>  save (@RequestBody @Valid SaveAuthRequestDto dto);

    @PutMapping("/update")
    public ResponseEntity<Boolean> updateAuth(@RequestBody AuthUpdateRequestDto dto);


    @PostMapping("/update-role")
    public ResponseEntity<Boolean> updateRole(@RequestBody AuthRoleUpdateRequestDto dto);

    @PostMapping("/update-state")
    public ResponseEntity<Boolean> updateAuthState(@RequestBody AuthStateUpdateRequestDto dto);
    }
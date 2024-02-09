package com.bilgeadam.manager;

import com.bilgeadam.dto.request.AuthStateUpdateRequestDto;
import com.bilgeadam.dto.request.CreateAdminRequestDto;
import com.bilgeadam.dto.request.GetProfileByTokenRequestDto;
import com.bilgeadam.dto.request.UserSaveRequestDto;
import com.bilgeadam.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Microservis yapısında sevisleri bir birleri ile iletişime geçebilmeleri içi kullanılan yapıdır.
 * Genellikle bir control yapsına istek atılır ve tüm end-pointleri interface içinde tanımlanır.
 * iki parametresi vardır;
 * 1- url: istek atılacak olan end point in adresi bulunur. root path buraya yazılır. (www.adres.com/userprofile) gibi
 * 2- name: her feignClint için benzersiz bir isimlendirme yapılır. isim yazımı işlevselliğe göre verilir.
 */
@FeignClient(url = "http://localhost:9092/api/v1/user",name = "userManager")
public interface UserManager {

    @GetMapping("/get-message")
    public String getMessage();
    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody @Valid UserSaveRequestDto dto);
    @PutMapping("/update-user-state")
    public ResponseEntity<Boolean> updateUserState(@RequestBody @Valid AuthStateUpdateRequestDto dto);
    @PostMapping("/create-admin")
    public ResponseEntity<Boolean> createAdmin(@RequestBody @Valid CreateAdminRequestDto dto);
    @PostMapping("/get-profile")
    public ResponseEntity<UserResponseDto> getProfileByToken(@RequestBody @Valid GetProfileByTokenRequestDto dto);

    @PutMapping("/update-user-state-for-password")
    public ResponseEntity<Boolean> updateUserStateForPassword(@RequestBody Long id);

}

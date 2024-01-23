package com.bilgeadam.manager;

import com.bilgeadam.dto.request.UserSaveRequestDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Microservis yapısında sevisleri bir birleri ile iletişime geçebilmeleri içi kullanılan yapıdır.
 * Genellikle bir control yapsına istek atılır ve tüm end-pointleri interface içinde tanımlanır.
 * iki parametresi vardır;
 * 1- url: istek atılacak olan end point in adresi bulunur. root path buraya yazılır. (www.adres.com/userprofile) gibi
 * 2- name: her feignClint için benzersiz bir isimlendirme yapılır. isim yazımı işlevselliğe göre verilir.
 */
@FeignClient(url = "http://localhost:9092/api/v1/user",name = "userManager")
public interface UserManager {

    @GetMapping("/getmessage")
    public String getMessage();
    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody @Valid UserSaveRequestDto dto);


}

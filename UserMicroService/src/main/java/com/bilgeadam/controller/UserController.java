package com.bilgeadam.controller;

import com.bilgeadam.dto.request.GetProfileByTokenRequestDto;
import com.bilgeadam.dto.request.UserSaveRequestDto;
import com.bilgeadam.dto.request.UserUpdateRequestDto;
import com.bilgeadam.dto.response.UserResponseDto;
import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.bilgeadam.constants.RestApiUrls.USER;

@RestController
@RequestMapping(USER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/getMessage")
    public String getMessage(){
        return "This is User Service";
    }

    @PostMapping("/save")
    @CrossOrigin("*")
    public ResponseEntity<Void> save(@RequestBody @Valid UserSaveRequestDto dto){
        UserProfile userProfile =   userService.save(dto);
        return ResponseEntity.ok().build();
    }



    @PutMapping("/update")
    @CrossOrigin("*")
    public ResponseEntity<Boolean> update(@RequestBody @Valid UserUpdateRequestDto dto){
        return ResponseEntity.ok(userService.update(dto));
    }

    @PostMapping("/getProfile")
    @CrossOrigin("*")
    public ResponseEntity<UserResponseDto> getProfileByToken(@RequestBody @Valid GetProfileByTokenRequestDto dto) {
        return ResponseEntity.ok(userService.getProfileByToken(dto));
    }
}

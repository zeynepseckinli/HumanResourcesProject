package com.bilgeadam.controller;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.UserResponseDto;
import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.bilgeadam.constants.RestApiUrls.*;

@RestController
@RequestMapping(USER)
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @GetMapping("/getMessage")
    public String getMessage() {
        return "This is User Service";
    }

//    @PostMapping(SAVE)
//    @CrossOrigin("*")
//    public ResponseEntity<Void> save(@RequestBody @Valid UserSaveRequestDto dto) {
//        UserProfile userProfile = userService.saveUser(dto);
//        return ResponseEntity.ok().build();
//    }
    @PostMapping("/createUser")
    @CrossOrigin("*")
    public ResponseEntity<Boolean> createUser(@RequestBody @Valid CreateUserRequestDto dto){
        return ResponseEntity.ok(userService.createUser(dto));
    }


    @PutMapping("/update")
    @CrossOrigin("*")
    public ResponseEntity<Boolean> update(@RequestBody @Valid UserUpdateRequestDto dto) {
        return ResponseEntity.ok(userService.updateUser(dto));
    }

    @PostMapping("/getProfile")
    @CrossOrigin("*")
    public ResponseEntity<UserResponseDto> getProfileByToken(@RequestBody @Valid GetProfileByTokenRequestDto dto) {
        return ResponseEntity.ok(userService.getProfileByToken(dto));
    }

    @Hidden
    @PutMapping("/updateUserState")
    public ResponseEntity<Boolean> updateUserState(@RequestBody @Valid AuthStateUpdateRequestDto dto) {
        return ResponseEntity.ok(userService.updateUserState(dto));
    }

    @PutMapping("/updateUserRole")
    public ResponseEntity<Boolean> updateUserRole(@RequestBody @Valid AuthRoleUpdateRequestDto dto) {
        return ResponseEntity.ok(userService.updateUserRole(dto));
    }

    @PostMapping(value = "/imageUpload", consumes = "multipart/form-data")
    public ResponseEntity<String> updateImage(@RequestParam("file") MultipartFile file, @RequestParam("token") String token) throws IOException {
        return ResponseEntity.ok(userService.updateImage(file, token));
    }


    @PostMapping("/createAdvance")
    public ResponseEntity<Boolean> createAdvance(@RequestBody @Valid CreateAdvanceRequestDto dto) {
        return ResponseEntity.ok(userService.createAdvance(dto));
    }

    @PutMapping("/updateAdvanceState")
    public ResponseEntity<Boolean> updateAdvanceState (@RequestBody UpdateStateRequestDto dto) {
        return ResponseEntity.ok(userService.updateAdvanceState(dto));
    }


    @PostMapping("/createPermission")
    public ResponseEntity<Boolean> createPermission(@RequestBody @Valid CreatePermissionRequestDto dto) {
        return ResponseEntity.ok(userService.createPermission(dto));
    }

    @PostMapping("/updatePermissionState")
    public ResponseEntity<Boolean> updatePermissionState(@RequestBody UpdateStateRequestDto dto){
        return ResponseEntity.ok(userService.updatePermissionState(dto));
    }

}

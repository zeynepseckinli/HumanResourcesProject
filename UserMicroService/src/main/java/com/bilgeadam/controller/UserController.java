package com.bilgeadam.controller;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.UserResponseDto;
import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.bilgeadam.constants.RestApiUrls.*;

@RestController
@RequestMapping(USER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/getMessage")
    public String getMessage() {
        return "This is User Service";
    }

    @PostMapping(SAVE)
    @CrossOrigin("*")
    public ResponseEntity<Void> save(@RequestBody @Valid UserSaveRequestDto dto) {
        UserProfile userProfile = userService.saveUser(dto);
        return ResponseEntity.ok().build();
    }


    @PutMapping(UPDATE)
    @CrossOrigin("*")
    public ResponseEntity<Boolean> update(@RequestBody @Valid UserUpdateRequestDto dto) {
        return ResponseEntity.ok(userService.updateUser(dto));
    }

    @PostMapping("/getProfile")
    @CrossOrigin("*")
    public ResponseEntity<UserResponseDto> getProfileByToken(@RequestBody @Valid GetProfileByTokenRequestDto dto) {
        return ResponseEntity.ok(userService.getProfileByToken(dto));
    }

    @PostMapping("/createAdvance")
    public ResponseEntity<Boolean> createAdvance(@RequestBody @Valid CreateAdvanceRequestDto dto) {
        return ResponseEntity.ok(userService.createAdvance(dto));
    }


    @PostMapping("/createPermission")
    public ResponseEntity<?> createPermission(@RequestBody @Valid CreatePermissionRequestDto dto) {
        return ResponseEntity.ok(userService.createPermission(dto));
    }
}

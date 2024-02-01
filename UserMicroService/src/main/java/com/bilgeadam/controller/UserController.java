package com.bilgeadam.controller;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.*;
import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import java.util.List;

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


    //Yetki tanımlayabilmek için end-pointlerin üzerine @PreAuthorize("hasAuthority('MANAGER')")
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

    @PostMapping(value = "/updateUserImage", consumes = "multipart/form-data")
    public ResponseEntity<String> updateUserImage(@RequestParam("file") MultipartFile file, @RequestParam("token") String token) throws IOException {
        return ResponseEntity.ok(userService.updateUserImage(file, token));
    }


    @PostMapping("/createAdvance")
    public ResponseEntity<Boolean> createAdvance(@RequestBody @Valid CreateAdvanceRequestDto dto) {
        return ResponseEntity.ok(userService.createAdvance(dto));
    }

    @PutMapping("/updateAdvanceState")
    public ResponseEntity<Boolean> updateAdvanceState (@RequestBody UpdateStateRequestDto dto) {
        return ResponseEntity.ok(userService.updateAdvanceState(dto));
    }

    @GetMapping("/findAllAdvancesForRequestUser")
    public ResponseEntity<List<AdvanceListResponseDtoForRequestUser>> findAllAdvancesForRequestUser(String token){
        return ResponseEntity.ok(userService.findAllAdvancesForRequestUser(token));
    }

    @GetMapping("/findAllAdvancesForResponseUser")
    public ResponseEntity<List<AdvanceListResponseDtoForResponseUser>> findAllAdvancesForResponseUser(String token){
        return ResponseEntity.ok(userService.findAllAdvancesForResponseUser(token));
    }


    @PostMapping("/createPermission")
    public ResponseEntity<Boolean> createPermission(@RequestBody @Valid CreatePermissionRequestDto dto) {
        return ResponseEntity.ok(userService.createPermission(dto));
    }

    @PutMapping("/updatePermissionState")
    public ResponseEntity<Boolean> updatePermissionState(@RequestBody UpdateStateRequestDto dto){
        return ResponseEntity.ok(userService.updatePermissionState(dto));
    }

    @GetMapping("/findAllPermissionsForRequestUser")
    public ResponseEntity<List<PermissionListResponseDtoForRequestUser>> findAllPermissionsForRequestUser(String token){
        return ResponseEntity.ok(userService.findAllPermissionsForRequestUser(token));
    }

    @GetMapping("/findAllPermissionsForResponseUser")
    public ResponseEntity<List<PermissionListResponseDtoForResponseUser>> findAllPermissionsForResponseUser(String token){
        return ResponseEntity.ok(userService.findAllPermissionsForResponseUser(token));
    }

    @PostMapping(value = "/create-expense")
    public ResponseEntity<Boolean> createExpense(@RequestBody CreateExpenseRequestDto dto){
        return ResponseEntity.ok(userService.createExpense(dto));
    }

    @PutMapping("/updateExpenseState")
    public ResponseEntity<Boolean> updateExpenseState(@RequestBody UpdateStateRequestDto dto){
        return ResponseEntity.ok(userService.updateExpenseState(dto));
    }

    @GetMapping("/findAllExpensesForRequestUser")
    public ResponseEntity<List<ExpensesListResponseDtoForRequestUser>> findAllExpensesForRequestUser(String token){
        return ResponseEntity.ok(userService.findAllExpensesForRequestUser(token));
    }

    @GetMapping("/findAllExpensesForResponseUser")
    public ResponseEntity<List<ExpensesListResponseDtoForResponseUser>> findAllExpensesForResponseUser(String token){
        return ResponseEntity.ok(userService.findAllExpensesForResponseUser(token));
    }


}

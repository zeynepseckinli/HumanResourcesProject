package com.bilgeadam.controller;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.*;
import com.bilgeadam.repository.CompanyRepository;
import com.bilgeadam.repository.entity.Company;
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

import static com.bilgeadam.constants.RestApiUrls.*;

@RestController
@RequestMapping(USER)
@RequiredArgsConstructor
@CrossOrigin("/api/v1/auth")
public class UserController {

    private final UserService userService;
    private final CompanyRepository companyRepository;

    @GetMapping("/get-message")
    public String getMessage() {
        return "This is User Service";
    }

//    @PostMapping(SAVE)
//    @CrossOrigin("*")
//    public ResponseEntity<Void> save(@RequestBody @Valid UserSaveRequestDto dto) {
//        UserProfile userProfile = userService.saveUser(dto);
//        return ResponseEntity.ok().build();
//    }
    @PostMapping("/create-user")
    public ResponseEntity<Boolean> createUser(@RequestBody @Valid CreateUserRequestDto dto){
        return ResponseEntity.ok(userService.createUser(dto));
    }


    @PutMapping("/update-user")
    public ResponseEntity<Boolean> updateUser(@RequestBody @Valid UserUpdateRequestDto dto) {
        return ResponseEntity.ok(userService.updateUser(dto));
    }

    @PostMapping("/get-profile")
    public ResponseEntity<UserResponseDto> getProfileByToken(@RequestBody @Valid GetProfileByTokenRequestDto dto) {
        return ResponseEntity.ok(userService.getProfileByToken(dto));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Boolean> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDto dto){
        return ResponseEntity.ok(userService.forgotPassword(dto));
    }

    @Hidden
    @PutMapping("/update-user-state")
    public ResponseEntity<Boolean> updateUserState(@RequestBody @Valid AuthStateUpdateRequestDto dto) {
        return ResponseEntity.ok(userService.updateUserState(dto));
    }

    @PutMapping("/update-user-role")
    public ResponseEntity<Boolean> updateUserRole(@RequestBody @Valid AuthRoleUpdateRequestDto dto) {
        return ResponseEntity.ok(userService.updateUserRole(dto));
    }

    @PostMapping(value = "/update-user-image", consumes = "multipart/form-data")
    public ResponseEntity<String> updateUserImage(@RequestParam("file") MultipartFile file, @RequestParam("token") String token) throws IOException {
        return ResponseEntity.ok(userService.updateUserImage(file, token));
    }


    @PostMapping("/create-advance")
    public ResponseEntity<Boolean> createAdvance(@RequestBody @Valid CreateAdvanceRequestDto dto) {
        return ResponseEntity.ok(userService.createAdvance(dto));
    }

    @PutMapping("/update-advance-state")
    public ResponseEntity<Boolean> updateAdvanceState (@RequestBody UpdateStateRequestDto dto) {
        return ResponseEntity.ok(userService.updateAdvanceState(dto));
    }

    @GetMapping("/find-all-advances-for-request-user")
    public ResponseEntity<List<AdvanceListResponseDtoForRequestUser>> findAllAdvancesForRequestUser(String token){
        return ResponseEntity.ok(userService.findAllAdvancesForRequestUser(token));
    }

    @GetMapping("/find-all-advances-for-response-user")
    public ResponseEntity<List<AdvanceListResponseDtoForResponseUser>> findAllAdvancesForResponseUser(String token){
        return ResponseEntity.ok(userService.findAllAdvancesForResponseUser(token));
    }


    @PostMapping("/create-permission")
    public ResponseEntity<Boolean> createPermission(@RequestBody @Valid CreatePermissionRequestDto dto) {
        return ResponseEntity.ok(userService.createPermission(dto));
    }

    @PutMapping("/update-permission-state")
    public ResponseEntity<Boolean> updatePermissionState(@RequestBody UpdateStateRequestDto dto){
        return ResponseEntity.ok(userService.updatePermissionState(dto));
    }

    @GetMapping("/find-all-permissions-for-request-user")
    public ResponseEntity<List<PermissionListResponseDtoForRequestUser>> findAllPermissionsForRequestUser(String token){
        return ResponseEntity.ok(userService.findAllPermissionsForRequestUser(token));
    }

    @GetMapping("/find-all-permissions-for-response-user")
    public ResponseEntity<List<PermissionListResponseDtoForResponseUser>> findAllPermissionsForResponseUser(String token){
        return ResponseEntity.ok(userService.findAllPermissionsForResponseUser(token));
    }

    @PostMapping(value = "/create-expense")
    public ResponseEntity<Boolean> createExpense(@RequestBody CreateExpenseRequestDto dto){
        return ResponseEntity.ok(userService.createExpense(dto));
    }

    @PostMapping(value = "/update-expense-image", consumes = "multipart/form-data")
    public ResponseEntity<String> updateExpenseImage(@RequestParam("file") MultipartFile file, @RequestParam("token") String token, @RequestParam("id") String id) throws IOException {
        return ResponseEntity.ok(userService.updateExpenseImage(file, token,id));
    }

    @PutMapping("/update-expense-state")
    public ResponseEntity<Boolean> updateExpenseState(@RequestBody UpdateStateRequestDto dto){
        return ResponseEntity.ok(userService.updateExpenseState(dto));
    }

    @GetMapping("/find-all-expenses-for-request-user")
    public ResponseEntity<List<ExpensesListResponseDtoForRequestUser>> findAllExpensesForRequestUser(String token){
        return ResponseEntity.ok(userService.findAllExpensesForRequestUser(token));
    }

    @GetMapping("/find-all-expenses-for-response-user")
    public ResponseEntity<List<ExpensesListResponseDtoForResponseUser>> findAllExpensesForResponseUser(String token){
        return ResponseEntity.ok(userService.findAllExpensesForResponseUser(token));
    }

    @PostMapping("/create-company")
    public ResponseEntity<Boolean> createCompany(@RequestBody companydto dto){
        Company company = Company.builder()
                .name(dto.getName())
                .build();
        companyRepository.save(company);
        return ResponseEntity.ok(true);
    }

}

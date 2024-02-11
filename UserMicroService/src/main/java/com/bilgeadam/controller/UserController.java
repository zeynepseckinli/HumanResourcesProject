package com.bilgeadam.controller;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.*;
import com.bilgeadam.repository.CompanyRepository;
import com.bilgeadam.repository.entity.Company;
import com.bilgeadam.service.UserService;
import com.bilgeadam.utility.enums.EState;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @Hidden
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
    public ResponseEntity<Boolean> createUser(@RequestBody @Valid CreateUserRequestDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PostMapping("/create-admin")
    public ResponseEntity<Boolean> createAdmin(@RequestBody @Valid CreateAdminRequestDto dto){
        return ResponseEntity.ok(userService.createAdmin(dto));
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
    public ResponseEntity<Boolean> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDto dto) {
        return ResponseEntity.ok(userService.forgotPassword(dto));
    }

    @Hidden
    @PutMapping("/update-user-state")
    public ResponseEntity<Boolean> updateUserState(@RequestBody @Valid AuthStateUpdateRequestDto dto) {
        return ResponseEntity.ok(userService.updateUserState(dto));
    }


    @Hidden
    @PutMapping("/update-user-state-for-password")
    public ResponseEntity<Boolean> updateUserStateForPassword(@RequestBody Long id){
        return ResponseEntity.ok(userService.updateUserStateForPassword(id));
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
    public ResponseEntity<Boolean> updateAdvanceState(@RequestBody UpdateStateRequestDto dto) {
        return ResponseEntity.ok(userService.updateAdvanceState(dto));
    }

    @PutMapping("/update-advance")
    public ResponseEntity<Boolean> updateAdvance(@RequestBody UpdateAdvanceRequestDto dto) {
        return ResponseEntity.ok(userService.updateAdvance(dto));
    }

    @GetMapping("/find-all-advances-for-request-user")
    public ResponseEntity<List<AdvanceListResponseDtoForRequestUser>> findAllAdvancesForRequestUser(String token) {
        return ResponseEntity.ok(userService.findAllAdvancesForRequestUser(token));
    }

    @GetMapping("/find-all-advances-for-response-user")
    public ResponseEntity<List<AdvanceListResponseDtoForResponseUser>> findAllAdvancesForResponseUser(String token) {
        return ResponseEntity.ok(userService.findAllAdvancesForResponseUser(token));
    }


    @PostMapping("/create-permission")
    public ResponseEntity<Boolean> createPermission(@RequestBody @Valid CreatePermissionRequestDto dto) {
        return ResponseEntity.ok(userService.createPermission(dto));
    }

    @PutMapping("/update-permission-state")
    public ResponseEntity<Boolean> updatePermissionState(@RequestBody UpdateStateRequestDto dto) {
        return ResponseEntity.ok(userService.updatePermissionState(dto));
    }

    @PutMapping("/update-permission")
    public ResponseEntity<Boolean> updatePermission(@RequestBody UpdatePermissionRequestDto dto) {
        return ResponseEntity.ok(userService.updatePermission(dto));
    }

    @GetMapping("/find-all-permissions-for-request-user")
    public ResponseEntity<List<PermissionListResponseDtoForRequestUser>> findAllPermissionsForRequestUser(String token) {
        return ResponseEntity.ok(userService.findAllPermissionsForRequestUser(token));
    }

    @GetMapping("/find-all-permissions-for-response-user")
    public ResponseEntity<List<PermissionListResponseDtoForResponseUser>> findAllPermissionsForResponseUser(String token) {
        return ResponseEntity.ok(userService.findAllPermissionsForResponseUser(token));
    }

    @PostMapping(value = "/create-expense")
    public ResponseEntity<Boolean> createExpense(@RequestBody CreateExpenseRequestDto dto) {
        return ResponseEntity.ok(userService.createExpense(dto));
    }

    @PostMapping(value = "/update-expense-image", consumes = "multipart/form-data")
    public ResponseEntity<String> updateExpenseImage(@RequestParam("file") MultipartFile file, @RequestParam("token") String token, @RequestParam("id") String id) throws IOException {
        return ResponseEntity.ok(userService.updateExpenseImage(file, token, id));
    }

    @PutMapping("/update-expense-state")
    public ResponseEntity<Boolean> updateExpenseState(@RequestBody UpdateStateRequestDto dto) {
        return ResponseEntity.ok(userService.updateExpenseState(dto));
    }

    @PutMapping("/update-expense")
    public ResponseEntity<Boolean> updateExpense(@RequestBody UpdateExpenseRequestDto dto) {
        return ResponseEntity.ok(userService.updateExpense(dto));
    }

    @GetMapping("/find-all-expenses-for-request-user")
    public ResponseEntity<List<ExpensesListResponseDtoForRequestUser>> findAllExpensesForRequestUser(String token) {
        return ResponseEntity.ok(userService.findAllExpensesForRequestUser(token));
    }

    @GetMapping("/find-all-expenses-for-response-user")
    public ResponseEntity<List<ExpensesListResponseDtoForResponseUser>> findAllExpensesForResponseUser(String token) {
        return ResponseEntity.ok(userService.findAllExpensesForResponseUser(token));
    }

    @PostMapping("/create-company")
    public ResponseEntity<Boolean> createCompany(@RequestBody CreateCompanyRequestDto dto) {
        return ResponseEntity.ok(userService.createCompany(dto));
    }

    @PutMapping("/update-company")
    public ResponseEntity<Boolean> updateCompany(@RequestBody UpdateCompanyRequestDto dto) {
        return ResponseEntity.ok(userService.updateCompany(dto));

    }

    @GetMapping("/get-company-details")
    public ResponseEntity<GetCompanyDetailsResponseDto> getDetailsCompany(GetCompanyDetailsRequestDto dto) {
        return ResponseEntity.ok(userService.getDetailsByCompanyId(dto));
    }

//    @GetMapping("/find-all-companies")
//    public ResponseEntity<List<Company>> findAll(String token) {
//        return ResponseEntity.ok(userService.findAllCompanies(token));
//    }

//    @DeleteMapping("/delete-company")
//    public ResponseEntity<Boolean> deleteCompany(@RequestBody @Valid String id, String token) {
//        return ResponseEntity.ok(userService.deleteCompany(id, token));
//    }


    @GetMapping("/find-all-companies")
    public ResponseEntity<List<Company>> findAll(String token, EState state) {
        return ResponseEntity.ok(userService.findAllCompanies(token, state));
    }

    @GetMapping("/find-all-employees-by-id")
    public ResponseEntity<List<UserResponseDto>> findAllUserByManagerId(GetProfileByTokenRequestDto dto){
        return ResponseEntity.ok(userService.findAllUserProfileByManagerId(dto));
    }


}

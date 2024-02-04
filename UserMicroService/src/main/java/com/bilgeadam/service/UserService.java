package com.bilgeadam.service;

import com.bilgeadam.config.CloudinaryConfig;
import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.*;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.exception.UserException;
import com.bilgeadam.manager.AuthManager;
import com.bilgeadam.mapper.*;
import com.bilgeadam.rabbitmq.producer.RegisterMailProducer;
import com.bilgeadam.repository.*;
import com.bilgeadam.repository.entity.*;
import com.bilgeadam.utility.JwtTokenManager;
import com.bilgeadam.utility.enums.ERole;
import com.bilgeadam.utility.enums.EState;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AdvanceRepository advanceRepository;
    private final AuthManager authManager;
    private final JwtTokenManager jwtTokenManager;
    private final PermissionRepository permissionRepository;
    private final CloudinaryConfig cloudinaryConfig;
    private final ExpenseRepository expenseRepository;
    private final RegisterMailProducer registerMailProducer;
    private final CompanyRepository companyRepository;


//    public UserProfile saveUser(UserSaveRequestDto dto) {
//        return userRepository.save(UserProfile.builder()
//                .email(dto.getEmail())
//                .authId(dto.getAuthId())
//                .build());
//    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Boolean createUser(CreateUserRequestDto dto) {
        userRepository.findOptionalByEmail(dto.getEmail())
                .ifPresent(userProfile -> {
                    throw new UserException(ErrorType.USERNAME_DUPLICATE);
                });
        //auth save
        String randomPass = generateRandomPassword(8);
        ResponseEntity<SaveAuthResponseDto> authDto = authManager.save(SaveAuthRequestDto.builder()
                .email(dto.getEmail())
                .password(randomPass)
                .build());

        SaveAuthResponseDto saveAuthResponseDto = authDto.getBody();
        UserProfile user = UserMapper.INSTANCE.fromCreateUserRequestDto(dto);
        user.setAuthId(saveAuthResponseDto.getAuthId());
        user.setActivationCode(randomPass);
        userRepository.save(user);
        registerMailProducer.sendActivationCode(UserMapper.INSTANCE.fromUserToRegisterModel(user));
        return true;
    }

    public static String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            password.append(characters.charAt(randomIndex));
        }
        return password.toString();
    }

    public UserResponseDto getProfileByToken(GetProfileByTokenRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        return UserMapper.INSTANCE.toUserResponseDto(user.get());
    }

    public Boolean updateUser(UserUpdateRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isPresent()) {
            userRepository.save(UserMapper.INSTANCE.fromUpdateDtoToUserProfile(dto, user.get()));
            authManager.updateAuth(AuthUpdateRequestDto.builder()
                    .authId(authId.get())
                    .email(dto.getEmail())
                    .build());
            return true;
        }
        throw new UserException(ErrorType.USER_NOT_FOUND);
    }

    public Boolean updateUserState(AuthStateUpdateRequestDto dto) {
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(dto.getAuthId());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        user.get().setState(dto.getSelectedState());
        user.get().setUpdateDate(LocalDate.now());
        userRepository.save(user.get());
        return true;
    }

    public Boolean updateUserRole(AuthRoleUpdateRequestDto dto) {
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(dto.getAuthId());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        user.get().setRole(dto.getSelectedRole());
        user.get().setUpdateDate(LocalDate.now());
        userRepository.save(user.get());
        authManager.updateRole(dto);
        return true;
    }


    public String updateUserImage(MultipartFile file, String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        String url = imageUpload(file);
        user.get().setAvatar(url);
        userRepository.save(user.get());
        return url;
    }


    public String imageUpload(MultipartFile file) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudinaryConfig.getCloud_name());
        config.put("api_key", cloudinaryConfig.getApi_key());
        config.put("api_secret", cloudinaryConfig.getApi_secret());

        Cloudinary cloudinary = new Cloudinary(config);

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String url = (String) result.get("url");
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Boolean createAdvance(CreateAdvanceRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        if (dto.getAdvanceAmount() > user.get().getSalary() * 3) {
            throw new UserException(ErrorType.ADVANCE_ERROR);
        }
        Advance advance = AdvanceMapper.INSTANCE.fromDto(dto);
        advance.setRequestUserId(user.get().getId());
        advance.setState(EState.PENDING);
        advanceRepository.save(advance);
        return true;
    }

    public Boolean updateAdvanceState(UpdateStateRequestDto dto) {
        Optional<Advance> advance = advanceRepository.findById(dto.getId());
        if (advance.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        advance.get().setState(dto.getSelectedState());
        advance.get().setResponseDate(LocalDate.now());
        advanceRepository.save(advance.get());
        return true;
    }

    public List<AdvanceListResponseDtoForRequestUser> findAllAdvancesForRequestUser(String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        return advanceRepository.findAllByRequestUserId(user.get().getId()).stream().map(advance -> {
            return AdvanceMapper.INSTANCE.toDtoForRequestUser(advance);
        }).collect(Collectors.toList());
    }

    public List<AdvanceListResponseDtoForResponseUser> findAllAdvancesForResponseUser(String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        return advanceRepository.findAllByResponseUserId(user.get().getId()).stream().map(advance -> {
            AdvanceListResponseDtoForResponseUser advanceList = AdvanceMapper.INSTANCE.toDtoForResponseUser(advance);
            UserProfile requestUser = userRepository.findById(advanceList.getRequestUserId()).get();
            advanceList.setName(requestUser.getName());
            advanceList.setSecondName(requestUser.getSecondName());
            advanceList.setSurname(requestUser.getSurname());
            advanceList.setSecondSurname(requestUser.getSecondSurname());
            advanceList.setEmail(requestUser.getEmail());
            advanceList.setSalary(requestUser.getSalary());
            return advanceList;
        }).collect(Collectors.toList());
    }


    public Boolean createPermission(CreatePermissionRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        Permission permission = PermissionMapper.INSTANCE.fromDto(dto);
        permission.setRequestUserId(user.get().getId());
        permission.setState(EState.PENDING);
        permissionRepository.save(permission);
        return true;
    }

    public Boolean updatePermissionState(UpdateStateRequestDto dto) {
        Optional<Permission> permission = permissionRepository.findById(dto.getId());
        if (permission.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        permission.get().setState(dto.getSelectedState());
        permission.get().setResponseDate(LocalDate.now());
        permissionRepository.save(permission.get());
        return true;
    }

    public List<PermissionListResponseDtoForRequestUser> findAllPermissionsForRequestUser(String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        return permissionRepository.findAllByRequestUserId(user.get().getId()).stream().map(permission -> {
            return PermissionMapper.INSTANCE.toDtoForRequestUser(permission);
        }).collect(Collectors.toList());
    }

    public List<PermissionListResponseDtoForResponseUser> findAllPermissionsForResponseUser(String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        return permissionRepository.findAllByResponseUserId(user.get().getId()).stream().map(permission -> {
            PermissionListResponseDtoForResponseUser permissionList = PermissionMapper.INSTANCE.toDtoForResponseUser(permission);
            UserProfile requestUser = userRepository.findById(permissionList.getRequestUserId()).get();
            permissionList.setName(requestUser.getName());
            permissionList.setSecondName(requestUser.getSecondName());
            permissionList.setSurname(requestUser.getSurname());
            permissionList.setSecondSurname(requestUser.getSecondSurname());
            permissionList.setEmail(requestUser.getEmail());
            return permissionList;
        }).collect(Collectors.toList());
    }

    public Boolean createExpense(CreateExpenseRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        // String url = imageUpload(file);
        Expense expense = ExpenseMapper.INSTANCE.fromDto(dto);
        expense.setRequestUserId(user.get().getId());
        expense.setState(EState.PENDING);
        // expense.setUrl(url);
        expenseRepository.save(expense);
        return true;
    }

    public Boolean updateExpenseState(UpdateStateRequestDto dto) {
        Optional<Expense> expense = expenseRepository.findById(dto.getId());
        if (expense.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        expense.get().setState(dto.getSelectedState());
        expense.get().setResponseDate(LocalDate.now());
        expenseRepository.save(expense.get());
        return true;
    }

    public List<ExpensesListResponseDtoForRequestUser> findAllExpensesForRequestUser(String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        return expenseRepository.findAllByRequestUserId(user.get().getId()).stream().map(expense -> {
            return ExpenseMapper.INSTANCE.toDtoForRequestUser(expense);
        }).collect(Collectors.toList());
    }

    public List<ExpensesListResponseDtoForResponseUser> findAllExpensesForResponseUser(String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        return expenseRepository.findAllByResponseUserId(user.get().getId()).stream().map(expense -> {
            ExpensesListResponseDtoForResponseUser expensesList = ExpenseMapper.INSTANCE.toDtoForResponseUser(expense);
            UserProfile requestUser = userRepository.findById(expensesList.getRequestUserId()).get();
            expensesList.setName(requestUser.getName());
            expensesList.setSecondName(requestUser.getSecondName());
            expensesList.setSurname(requestUser.getSurname());
            expensesList.setSecondSurname(requestUser.getSecondSurname());
            expensesList.setEmail(requestUser.getEmail());
            return expensesList;
        }).collect(Collectors.toList());
    }


    public Boolean createCompany(CreateCompanyRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isPresent() && user.get().getRole().equals(ERole.ADMIN)) {
            Company company = CompanyMapper.INSTANCE.toCompany(dto);
            companyRepository.save(company);
        } else {
            throw new UserException(ErrorType.NOT_AUTHORIZED);
        }
        return true;

    }


    public Boolean updateCompany(UpdateCompanyRequestDto dto) {
        Optional<Company> company = companyRepository.findById(dto.getId());
        if (company.isEmpty()) {
            throw new UserException(ErrorType.COMPANY_NOT_FOUND);
        }
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isPresent() && user.get().getRole().equals(ERole.ADMIN)) {
            company.get().setName(dto.getName());
            company.get().setTitle(dto.getTitle());
            company.get().setTaxNumber(dto.getTaxNumber());
            company.get().setAddress(dto.getAddress());
            company.get().setPhone(dto.getPhone());
            company.get().setEmail(dto.getEmail());
            company.get().setNumberOfEmployees(dto.getNumberOfEmployees());
            company.get().setYearOfEstablishment(dto.getYearOfEstablishment());
            companyRepository.save(company.get());
        } else {
            throw new UserException(ErrorType.NOT_AUTHORIZED);
        }
        return true;

    }


//    public Boolean deleteCompany(String id) {
//        Optional<Company> company = companyRepository.findById(id);
//        if (company.isEmpty()) {
//            throw new UserException(ErrorType.COMPANY_NOT_FOUND);
//        }
//        companyRepository.delete(company.get());
//        return true;
//    }


    public List<Company> findAllCompanies(String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isPresent() && user.get().getRole().equals(ERole.ADMIN)) {
            return companyRepository.findAll();
        } else {
            throw new UserException(ErrorType.NOT_AUTHORIZED);
        }
    }


}
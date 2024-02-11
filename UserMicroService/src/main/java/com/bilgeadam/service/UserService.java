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
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public Boolean createSuperAdmin(){
        Optional<List<UserProfile>> userProfileList = userRepository.findOptionalUserProfileByRole(ERole.SUPERADMIN);
        if (userProfileList.get().isEmpty()) {
            String randomPass = generateRandomPassword(8);
            String email = "superadmin@bilgeadmin.com";
            ResponseEntity<SaveAuthResponseDto> authDto = authManager.save(SaveAuthRequestDto.builder()
                    .email(email)
                    .password(randomPass)
                    .build());
            SaveAuthResponseDto saveAuthResponseDto = authDto.getBody();
            UserProfile user = new UserProfile();
            user.setAuthId(saveAuthResponseDto.getAuthId());
            user.setEmail(email);
            user.setActivationCode(randomPass);
            user.setState(EState.PENDING);
            user.setCreateDate(LocalDate.now());
            user.setUpdateDate(LocalDate.now());
            user.setRole(ERole.SUPERADMIN);
            authManager.updateRole(AuthRoleUpdateRequestDto.builder()
                    .authId(user.getAuthId())
                    .selectedRole(ERole.SUPERADMIN)
                    .build());
            userRepository.save(user);
        }
        return true;
    }


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Boolean createAdmin(CreateAdminRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        ERole roleByToken = jwtTokenManager.getRoleByToken(dto.getToken()).get();
        if (!(roleByToken == ERole.SUPERADMIN)) {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
        }
        String randomPass = generateRandomPassword(8);
        String email = generateEmailForAdmin(dto);
        ResponseEntity<SaveAuthResponseDto> authDto = authManager.save(SaveAuthRequestDto.builder()
                .email(email)
                .password(randomPass)
                .build());
        SaveAuthResponseDto saveAuthResponseDto = authDto.getBody();
        UserProfile user = UserMapper.INSTANCE.fromCreateAdminRequestDto(dto);
        user.setAuthId(saveAuthResponseDto.getAuthId());
        user.setEmail(email);
        user.setActivationCode(randomPass);
        user.setState(EState.PENDING);
        user.setCreateDate(LocalDate.now());
        user.setUpdateDate(LocalDate.now());
        user.setRole(ERole.ADMIN);
        authManager.updateRole(AuthRoleUpdateRequestDto.builder()
                    .authId(user.getAuthId())
                    .selectedRole(ERole.ADMIN)
                    .build());
        userRepository.save(user);

        registerMailProducer.sendActivationCode(UserMapper.INSTANCE.fromUserToRegisterModel(user));
        return true;
    }

    public String generateEmailForAdmin(CreateAdminRequestDto dto) {
        String name = dto.getName();
        String secondName = dto.getSecondName();
        String surname = dto.getSurname();
        String secondSurname = dto.getSecondSurname();
        String email = name + secondName + "." + surname + secondSurname + "@bilgeadmin.com";
        return convertToAscii(email.toLowerCase());
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Boolean createUser(CreateUserRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> userProfile = userRepository.findOptionalByAuthId(authId.get());
        if (userProfile.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        String randomPass = generateRandomPassword(8);
        String email = generateEmail(dto);
        ResponseEntity<SaveAuthResponseDto> authDto = authManager.save(SaveAuthRequestDto.builder()
                .email(email)
                .password(randomPass)
                .build());
        SaveAuthResponseDto saveAuthResponseDto = authDto.getBody();
        UserProfile user = UserMapper.INSTANCE.fromCreateUserRequestDto(dto);
        user.setAuthId(saveAuthResponseDto.getAuthId());
        user.setEmail(email);
        user.setActivationCode(randomPass);
        user.setState(EState.PENDING);
        user.setCreateDate(LocalDate.now());
        user.setUpdateDate(LocalDate.now());
        if (userProfile.get().getRole().equals(ERole.MANAGER)){
            user.setRole(ERole.EMPLOYEE);
            user.setManagerId(userProfile.get().getId());
            authManager.updateRole(AuthRoleUpdateRequestDto.builder()
                    .authId(user.getAuthId())
                    .selectedRole(ERole.EMPLOYEE)
                    .build());
        } else if (userProfile.get().getRole().equals(ERole.ADMIN)) {
            user.setRole(ERole.MANAGER);
            authManager.updateRole(AuthRoleUpdateRequestDto.builder()
                    .authId(user.getAuthId())
                    .selectedRole(ERole.MANAGER)
                    .build());
        }
        userRepository.save(user);

        registerMailProducer.sendActivationCode(UserMapper.INSTANCE.fromUserToRegisterModel(user));
        return true;
    }


    public String convertToAscii(String mail) {
        mail = mail.replaceAll("ı", "i").replaceAll("ğ", "g").replaceAll("ü", "u")
                .replaceAll("ş", "s").replaceAll("ö", "o").replaceAll("ç", "c")
                .replaceAll("İ", "I").replaceAll("Ğ", "G").replaceAll("Ü", "U")
                .replaceAll("Ş", "S").replaceAll("Ö", "O").replaceAll("Ç", "C");
        String regex = "[^a-zA-Z0-9._@-]";
        return mail.replaceAll(regex, "");
    }

    public String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            password.append(characters.charAt(randomIndex));
        }
        return password.toString();
    }

    public String generateEmail(CreateUserRequestDto dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Belirtilen CompanyId ile eşleşen bir şirket bulunamadı."));
        String username = dto.getName();
        String secondName = dto.getSecondName();
        String surname = dto.getSurname();
        String secondSurname = dto.getSecondSurname();
        String companyName = company.getName();
        String email = username + secondName + "." + surname + secondSurname + "@" + companyName + ".com";
        return convertToAscii(email.toLowerCase());
    }

    public Boolean forgotPassword(ForgotPasswordRequestDto dto) {

        Optional<UserProfile> user = userRepository.findByPersonalEmail(dto.getPersonalEmail());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        if (!user.get().getPhone().equals(dto.getPhone())) {
            throw new UserException(ErrorType.BAD_REQUEST_ERROR);
        }
        String randomPass = generateRandomPassword(8);
        user.get().setActivationCode(randomPass);
        authManager.updateAuth(AuthUpdateRequestDto.builder()
                .state(EState.PENDING)
                .authId(user.get().getAuthId())
                .password(randomPass)
                .build());
        user.get().setState(EState.PENDING);
        userRepository.save(user.get());
        registerMailProducer.sendActivationCode(UserMapper.INSTANCE.fromUserToRegisterModel(user.get()));
        return true;
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
//        Company company = companyRepository.findById(dto.getCompanyId())
//                .orElseThrow(() -> new RuntimeException("Belirtilen CompanyId ile eşleşen bir şirket bulunamadı."));
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isPresent() && (user.get().getRole() == ERole.MANAGER)) {
            Optional<UserProfile> userProfile = userRepository.findOptionalByAuthId(dto.getAuthId());
            if (userProfile.get().getManagerId().equals(user.get().getId())){
                userProfile.get().setName(dto.getName());
                userProfile.get().setSecondName(dto.getSecondName());
                userProfile.get().setSurname(dto.getSurname());
                userProfile.get().setSecondSurname(dto.getSecondSurname());
                userProfile.get().setPersonalEmail(dto.getPersonalEmail());
                userProfile.get().setIdentityNumber(dto.getIdentityNumber());
                userProfile.get().setBirthPlace(dto.getBirthPlace());
                userProfile.get().setOccupation(dto.getOccupation());
                userProfile.get().setDepartment(dto.getDepartment());
                userProfile.get().setAddress(dto.getAddress());
                userProfile.get().setPhone(dto.getPhone());
                userProfile.get().setSalary(dto.getSalary());
                userProfile.get().setBirthDate(dto.getBirthDate());
                userProfile.get().setJobStart(dto.getJobStart());
                userProfile.get().setJobEnd(dto.getJobEnd());
                userProfile.get().setAvatar(dto.getAvatar());

                userRepository.save(userProfile.get());
            }
            return true;
        }
        throw new UserException(ErrorType.USER_NOT_FOUND);
    }

    public Boolean updateUserState(AuthStateUpdateRequestDto dto) {
        Optional<Long> idByToken = jwtTokenManager.getIdByToken(dto.getToken());
        ERole roleByToken = jwtTokenManager.getRoleByToken(dto.getToken()).get();
        if (idByToken.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        } else if (!(roleByToken == ERole.ADMIN)) {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(dto.getAuthId());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        user.get().setState(dto.getSelectedState());
        user.get().setUpdateDate(LocalDate.now());
        userRepository.save(user.get());
        return true;
    }

    public Boolean updateUserStateForPassword(Long id) {
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(id);
        if (user.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        user.get().setState(EState.ACTIVE);
//        user.get().setUpdateDate(LocalDate.now());
        userRepository.save(user.get());
        return true;
    }


//    public Long tokenControl(String token, ERole role, EState state )///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Boolean updateUserRole(AuthRoleUpdateRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> userProfile = userRepository.findOptionalByAuthId(authId.get());
        if (userProfile.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        if (userProfile.get().getRole().equals(ERole.ADMIN)){
            Optional<UserProfile> user = userRepository.findOptionalByAuthId(dto.getAuthId());
            user.get().setRole(dto.getSelectedRole());
            user.get().setUpdateDate(LocalDate.now());
            userRepository.save(user.get());
            authManager.updateRole(dto);
            return true;
        }
        throw new UserException(ErrorType.AUTHORITY_ERROR);

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
        advance.setResponseUserId(user.get().getManagerId());
        advance.setState(EState.PENDING);
        advanceRepository.save(advance);
        return true;
    }

    public Boolean updateAdvanceState(UpdateStateRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        Optional<Advance> advance = advanceRepository.findById(dto.getId());
        if (advance.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        if (!(advance.get().getResponseUserId().equals(user.get().getId()))) {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
        }
        advance.get().setState(dto.getSelectedState());
        advance.get().setResponseDate(LocalDate.now());
        advanceRepository.save(advance.get());
        return true;
    }

    public Boolean updateAdvance(UpdateAdvanceRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        Optional<Advance> advance = advanceRepository.findById(dto.getId());
        if (advance.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        if (!(advance.get().getRequestUserId().equals(user.get().getId()))) {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
        }
        advance.get().setAdvanceAmount(dto.getAdvanceAmount());
        advance.get().setUnitOfCurrency(dto.getUnitOfCurrency());
        advance.get().setDescription(dto.getDescription());
        advance.get().setAdvanceType(dto.getAdvanceType());
        advance.get().setRequestDate(dto.getRequestDate());
        advance.get().setResponseUserId(user.get().getManagerId());
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
        permission.setResponseUserId(user.get().getManagerId());
        permission.setRequestDate(LocalDate.now());
        permission.setState(EState.PENDING);
        permissionRepository.save(permission);
        return true;
    }


    public Boolean updatePermissionState(UpdateStateRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        Optional<Permission> permission = permissionRepository.findById(dto.getId());
        if (permission.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        if (!(permission.get().getResponseUserId().equals(user.get().getId()))) {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
        }
        permission.get().setState(dto.getSelectedState());
        permission.get().setResponseUserId(user.get().getManagerId());
        permission.get().setResponseDate(LocalDate.now());
        permissionRepository.save(permission.get());
        return true;
    }

    public Boolean updatePermission(UpdatePermissionRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        Optional<Permission> permission = permissionRepository.findById(dto.getId());
        if (permission.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        if (!(permission.get().getRequestUserId().equals(user.get().getId()))) {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
        }
        permission.get().setPermissionType(dto.getPermissionType());
        permission.get().setPermissionStart(dto.getPermissionStart());
        permission.get().setPermissionEnd(dto.getPermissionEnd());
        permission.get().setPermissionDuration(dto.getPermissionDuration());
        permission.get().setResponseUserId(user.get().getManagerId());
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
        expense.setResponseUserId(user.get().getManagerId());
        expense.setState(EState.PENDING);
        // expense.setUrl(url);
        expenseRepository.save(expense);
        return true;
    }

    public String updateExpenseImage(MultipartFile file, String token, String id) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        Optional<Expense> expense = expenseRepository.findById(id);
        if (!(expense.get().getRequestUserId().equals(user.get().getId()))) {
            throw new UserException(ErrorType.BAD_REQUEST);
        }
        String url = imageUpload(file);
        expense.get().setUrl(url);
        expenseRepository.save(expense.get());
        return url;
    }

    public Boolean updateExpenseState(UpdateStateRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        Optional<Expense> expense = expenseRepository.findById(dto.getId());
        if (expense.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        if (!(expense.get().getResponseUserId().equals(user.get().getId()))) {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
        }
        expense.get().setState(dto.getSelectedState());
        expense.get().setResponseUserId(user.get().getManagerId());
        expense.get().setResponseDate(LocalDate.now());
        expenseRepository.save(expense.get());
        return true;
    }

    public Boolean updateExpense(UpdateExpenseRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        Optional<Expense> expense = expenseRepository.findById(dto.getId());
        if (expense.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        if (!(expense.get().getRequestUserId().equals(user.get().getId()))) {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
        }
        expense.get().setRequestDate(dto.getRequestDate());
        expense.get().setDescription(dto.getDescription());
        expense.get().setExpenseAmount(dto.getExpenseAmount());
        expense.get().setExpenseType(dto.getExpenseType());
        expense.get().setUnitOfCurrency(dto.getUnitOfCurrency());
        expense.get().setResponseUserId(user.get().getManagerId());
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
            company.setCreateDate(LocalDate.now());
            company.setUpdateDate(LocalDate.now());
            company.setState(EState.ACTIVE);
            companyRepository.save(company);
        } else {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
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
            company.get().setUpdateDate(LocalDate.now());
            company.get().setState(dto.getState());
            companyRepository.save(company.get());
        } else {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
        }
        return true;
    }


//    public Boolean deleteCompany(String id, String token) {
//        Optional<Company> company = companyRepository.findById(id);
//        if (company.isEmpty()) {
//            throw new UserException(ErrorType.COMPANY_NOT_FOUND);
//        }
//        Optional<Long> authId = jwtTokenManager.getIdByToken(id);
//        if (authId.isEmpty()) {
//            throw new UserException(ErrorType.INVALID_TOKEN);
//        }
//        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
//        if (user.isPresent() && user.get().getRole().equals(ERole.ADMIN)) {
//            company.get().setState(EState.DELETED);
//            companyRepository.save(company.get());
//
//        }
//        return true;
//
//    }

    public GetCompanyDetailsResponseDto getDetailsByCompanyId(GetCompanyDetailsRequestDto dto) {
        Optional<Company> company = companyRepository.findById(dto.getCompanyId());
        if (company.isEmpty()) {
            throw new UserException(ErrorType.COMPANY_NOT_FOUND);
        }
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isPresent() && user.get().getRole().equals(ERole.ADMIN)) {
            return CompanyMapper.INSTANCE.toCompanyResponseDto(company.get());
        } else {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
        }
    }


//    public List<Company> findAllCompanies(String token) {
//        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
//        if (authId.isEmpty()) {
//            throw new UserException(ErrorType.INVALID_TOKEN);
//        }
//        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
//        if (user.isPresent() && user.get().getRole().equals(ERole.ADMIN)) {
//            return companyRepository.findAllByStateNot(EState.DELETED);
//        } else {
//            throw new UserException(ErrorType.AUTHORITY_ERROR);
//        }
//    }



    public List<Company> findAllCompanies(String token, EState state) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isPresent() && user.get().getRole().equals(ERole.ADMIN)) {
            return companyRepository.findAllByState(state);
        } else {
            throw new UserException(ErrorType.AUTHORITY_ERROR);
        }
    }


    public List<UserResponseDto> findAllUserProfileByManagerId(GetProfileByTokenRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()){
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> manager = userRepository.findOptionalByAuthId(authId.get());
        if (manager.isPresent() && manager.get().getRole().equals(ERole.MANAGER)){
            List<UserProfile> users = userRepository.findAllUserProfileByManagerId(manager.get().getId());
            return users.stream()
                    .map(user -> UserMapper.INSTANCE.toUserResponseDto(user))
                    .collect(Collectors.toList());
        }
        throw new UserException(ErrorType.AUTHORITY_ERROR);
    }


}

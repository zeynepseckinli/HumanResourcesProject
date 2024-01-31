package com.bilgeadam.service;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.CreateCompanyResponseDto;
import com.bilgeadam.dto.response.SaveAuthResponseDto;
import com.bilgeadam.dto.response.UserResponseDto;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.exception.UserException;
import com.bilgeadam.manager.AuthManager;
import com.bilgeadam.mapper.AdvanceMapper;
import com.bilgeadam.mapper.CompanyMapper;
import com.bilgeadam.mapper.PermissionMapper;
import com.bilgeadam.mapper.UserMapper;
import com.bilgeadam.repository.AdvanceRepository;
import com.bilgeadam.repository.CompanyRepository;
import com.bilgeadam.repository.PermissionRepository;
import com.bilgeadam.repository.UserRepository;
import com.bilgeadam.repository.entity.Advance;
import com.bilgeadam.repository.entity.Company;
import com.bilgeadam.repository.entity.Permission;
import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.utility.JwtTokenManager;
import com.bilgeadam.utility.enums.EState;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final AdvanceRepository advanceRepository;
    private final AuthManager authManager;
    private final JwtTokenManager jwtTokenManager;
    private final PermissionRepository permissionRepository;
    private final CompanyRepository companyRepository;




//    public UserProfile saveUser(UserSaveRequestDto dto) {
//        return userRepository.save(UserProfile.builder()
//                .email(dto.getEmail())
//                .authId(dto.getAuthId())
//                .build());
//    }

    public Boolean createUser(CreateUserRequestDto dto){
        userRepository.findOptionalByEmail(dto.getEmail())
                .ifPresent(userProfile -> {
                    throw new UserException(ErrorType.USERNAME_DUPLICATE);
                });
        //auth save
        ResponseEntity<SaveAuthResponseDto> authDto = authManager.save(SaveAuthRequestDto.builder()
                        .email(dto.getEmail())
                        .password(generateRandomPassword(8))
                .build());
        SaveAuthResponseDto saveAuthResponseDto = authDto.getBody();
        UserProfile user = UserMapper.INSTANCE.fromCreateUserRequestDto(dto);
        user.setAuthId(saveAuthResponseDto.getAuthId());
        userRepository.save(user);
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

    public Boolean updateUserState(AuthStateUpdateRequestDto dto){
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(dto.getAuthId());
        if (user.isEmpty()){
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        user.get().setState(dto.getSelectedState());
        user.get().setUpdateDate(LocalDate.now());
        userRepository.save(user.get());
        return true;
    }

    public Boolean updateUserRole(AuthRoleUpdateRequestDto dto){
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(dto.getAuthId());
        if (user.isEmpty()){
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        user.get().setRole(dto.getSelectedRole());
        user.get().setUpdateDate(LocalDate.now());
        userRepository.save(user.get());
        authManager.updateRole(dto);
        return true;
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
        advance.setState(EState.PENDING);
        advanceRepository.save(advance);
        return true;
    }

    public Boolean updateAdvanceState(UpdateStateRequestDto dto) {
        Optional<Advance> advance = advanceRepository.findById(dto.getId());
        if(advance.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        advance.get().setState(dto.getApprovalState());
        advance.get().setResponseDate(LocalDate.now());
        advanceRepository.save(advance.get());
        return true;
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
//        permission.setPermissionDuration(dto.getPermissionDuration());
//        permission.setStartOfPermission(dto.getStartOfPermission());
//        permission.setEndOfPermission(dto.getEndOfPermission());
        permission.setState(EState.PENDING);
        permissionRepository.save(permission);
        return true;
    }

    public Boolean updatePermissionState(UpdateStateRequestDto dto) {
        Optional<Permission> permission = permissionRepository.findById(dto.getId());
        if(permission.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        permission.get().setState(dto.getApprovalState());
        permission.get().setResponseDate(LocalDate.now());
        permissionRepository.save(permission.get());
        return true;
    }


    public Optional<UserProfile> findByAuthId(Long authId) {
        return userRepository.findOptionalByAuthId(authId);
    }

    public Boolean createCompany(CreateCompanyResponseDto dto) {
        Company company = CompanyMapper.INSTANCE.toCompany(dto);
        Optional<Company> company2 = companyRepository.findOptionalByCompanyName(dto.getCompanyName());
        if (company2.isPresent()) {
            throw new UserException(ErrorType.COMPANY_ALREADY_EXIST);
        }
        companyRepository.save(company);
        return true;
    }


}

package com.bilgeadam.service;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.SaveAuthResponseDto;
import com.bilgeadam.dto.response.UserResponseDto;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.exception.UserException;
import com.bilgeadam.manager.AuthManager;
import com.bilgeadam.mapper.AdvanceMapper;
import com.bilgeadam.mapper.PermissionMapper;
import com.bilgeadam.mapper.UserMapper;
import com.bilgeadam.repository.AdvanceRepository;
import com.bilgeadam.repository.PermissionRepository;
import com.bilgeadam.repository.UserRepository;
import com.bilgeadam.repository.entity.Advance;
import com.bilgeadam.repository.entity.Permission;
import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.utility.JwtTokenManager;
import com.bilgeadam.utility.enums.EState;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDate;
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
        advance.setAdvanceState(EState.PENDING);
        advanceRepository.save(advance);
        return true;
    }

    public Boolean updateAdvanceState(UpdateStateRequestDto dto) {
        Optional<Advance> advance = advanceRepository.findById(dto.getId());
        if(advance.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        advance.get().setAdvanceState(dto.getApprovalState());
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
        permission.setPermissionState(EState.PENDING);
        permissionRepository.save(permission);
        return true;
    }

    public Boolean updatePermissionState(UpdateStateRequestDto dto) {
        Optional<Permission> permission = permissionRepository.findById(dto.getId());
        if(permission.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        permission.get().setPermissionState(dto.getApprovalState());
        permission.get().setDateOfResponse(LocalDate.now());
        permissionRepository.save(permission.get());
        return true;
    }



}

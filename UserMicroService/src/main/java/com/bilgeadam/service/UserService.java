package com.bilgeadam.service;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.UserResponseDto;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.exception.UserException;
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
import com.bilgeadam.utility.enums.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final AdvanceRepository advanceRepository;

    private final JwtTokenManager jwtTokenManager;
    private final PermissionRepository permissionRepository;

    public UserProfile saveUser(UserSaveRequestDto dto) {
        return userRepository.save(UserProfile.builder()
                .email(dto.getEmail())
                .authId(dto.getAuthId())
                .build());
    }

    public Boolean createUser(CreateUserRequestDto dto){
        UserProfile user = UserMapper.INSTANCE.fromCreateUserRequestDto(dto);
        userRepository.save(user);
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
        Optional<Long> authId = jwtTokenManager.getIdByToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        userRepository.save(UserMapper.INSTANCE.toUser(dto));
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
        advanceRepository.save(advance);
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
        permission.setPermissionState(State.PENDING);
        permissionRepository.save(permission);
        return true;
    }
}

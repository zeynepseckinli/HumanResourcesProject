package com.bilgeadam.service;

import com.bilgeadam.dto.request.CreateAdvanceRequestDto;
import com.bilgeadam.dto.request.GetProfileByTokenRequestDto;
import com.bilgeadam.dto.request.UserSaveRequestDto;
import com.bilgeadam.dto.request.UserUpdateRequestDto;
import com.bilgeadam.dto.response.UserResponseDto;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.exception.UserException;
import com.bilgeadam.mapper.AdvanceMapper;
import com.bilgeadam.mapper.UserMapper;
import com.bilgeadam.repository.AdvanceRepository;
import com.bilgeadam.repository.UserRepository;
import com.bilgeadam.repository.entity.Advance;
import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final AdvanceRepository advanceRepository;

    private final JwtTokenManager jwtTokenManager;

    public UserProfile save(UserSaveRequestDto dto) {
        return userRepository.save(UserProfile.builder()
                .email(dto.getEmail())
                .authId(dto.getAuthId())
                .build());
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

    public Boolean update(UserUpdateRequestDto dto) {
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
}

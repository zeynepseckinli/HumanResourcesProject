package com.bilgeadam.service;

import com.bilgeadam.config.CloudinaryConfig;
import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.*;
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
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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


    public String updateUserImage(MultipartFile file, String token){
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


    public String imageUpload(MultipartFile file){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudinaryConfig.getCloud_name());
        config.put("api_key", cloudinaryConfig.getApi_key());
        config.put("api_secret", cloudinaryConfig.getApi_secret());

        Cloudinary cloudinary = new Cloudinary(config);

        try {
            Map<?,?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String url = (String) result.get("url");
            return url;
        }catch (Exception e){
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
        if(advance.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        advance.get().setState(dto.getSelectedState());
        advance.get().setResponseDate(LocalDate.now());
        advanceRepository.save(advance.get());
        return true;
    }

    public List<AdvanceListResponseDtoForRequestUser> findAllAdvancesForRequestUser (String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        return advanceRepository.findAllByRequestUserId(user.get().getId()).stream().map(advance -> {
            return AdvanceListResponseDtoForRequestUser.builder()
                    .advanceAmount(advance.getAdvanceAmount())
                    .unitOfCurrency(advance.getUnitOfCurrency())
                    .description(advance.getDescription())
                    .advanceType(advance.getAdvanceType())
                    .requestDate(advance.getRequestDate())
                    .responseDate(advance.getResponseDate())
                    .responseUserId(advance.getResponseUserId())
                    .state(advance.getState())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<AdvanceListResponseDtoForResponseUser> findAllAdvancesForResponseUser (String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        return advanceRepository.findAllByResponseUserId(user.get().getId()).stream().map(advance -> {
            AdvanceListResponseDtoForResponseUser advanceListResponseDtoForResponseUser = AdvanceListResponseDtoForResponseUser.builder()
                    .requestUserId(advance.getRequestUserId())
                    .advanceAmount(advance.getAdvanceAmount())
                    .unitOfCurrency(advance.getUnitOfCurrency())
                    .description(advance.getDescription())
                    .advanceType(advance.getAdvanceType())
                    .requestDate(advance.getRequestDate())
                    .responseDate(advance.getResponseDate())
                    .state(advance.getState())
                    .build();
            UserProfile requestUser = userRepository.findById(advanceListResponseDtoForResponseUser.getRequestUserId()).get();
            advanceListResponseDtoForResponseUser.setName(requestUser.getName());
            advanceListResponseDtoForResponseUser.setSecondName(requestUser.getSecondName());
            advanceListResponseDtoForResponseUser.setSurname(requestUser.getSurname());
            advanceListResponseDtoForResponseUser.setSecondSurname(requestUser.getSecondSurname());
            advanceListResponseDtoForResponseUser.setEmail(requestUser.getEmail());
            advanceListResponseDtoForResponseUser.setSalary(requestUser.getSalary());
            return advanceListResponseDtoForResponseUser;
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
        if(permission.isEmpty()) {
            throw new UserException(ErrorType.REQUEST_NOT_FOUND);
        }
        permission.get().setState(dto.getSelectedState());
        permission.get().setResponseDate(LocalDate.now());
        permissionRepository.save(permission.get());
        return true;
    }

    public List<PermissionListResponseDtoForRequestUser> findAllPermissionsForRequestUser (String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        return permissionRepository.findAllByRequestUserId(user.get().getId()).stream().map(permission -> {
            return PermissionListResponseDtoForRequestUser.builder()
                    .responseUserId(permission.getResponseUserId())
                    .permissionType(permission.getPermissionType())
                    .permissionStart(permission.getPermissionStart())
                    .permissionEnd(permission.getPermissionEnd())
                    .requestDate(permission.getRequestDate())
                    .responseDate(permission.getResponseDate())
                    .permissionDuration(permission.getPermissionDuration())
                    .state(permission.getState())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<PermissionListResponseDtoForResponseUser> findAllPermissionsForResponseUser (String token) {
        Optional<Long> authId = jwtTokenManager.getIdByToken(token);
        if (authId.isEmpty()) {
            throw new UserException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> user = userRepository.findOptionalByAuthId(authId.get());
        if (user.isEmpty()) {
            throw new UserException(ErrorType.USER_NOT_FOUND);
        }
        return permissionRepository.findAllByResponseUserId(user.get().getId()).stream().map(permission -> {
            PermissionListResponseDtoForResponseUser permissionListResponseDtoForResponseUser = PermissionListResponseDtoForResponseUser.builder()
                    .requestUserId(permission.getRequestUserId())
                    .permissionType(permission.getPermissionType())
                    .permissionStart(permission.getPermissionStart())
                    .permissionEnd(permission.getPermissionEnd())
                    .permissionDuration(permission.getPermissionDuration())
                    .requestDate(permission.getRequestDate())
                    .responseDate(permission.getResponseDate())
                    .state(permission.getState())
                    .build();
            UserProfile requestUser = userRepository.findById(permissionListResponseDtoForResponseUser.getRequestUserId()).get();
            permissionListResponseDtoForResponseUser.setName(requestUser.getName());
            permissionListResponseDtoForResponseUser.setSecondName(requestUser.getSecondName());
            permissionListResponseDtoForResponseUser.setSurname(requestUser.getSurname());
            permissionListResponseDtoForResponseUser.setSecondSurname(requestUser.getSecondSurname());
            permissionListResponseDtoForResponseUser.setEmail(requestUser.getEmail());
            return permissionListResponseDtoForResponseUser;
        }).collect(Collectors.toList());
    }

}

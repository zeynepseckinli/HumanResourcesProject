package com.bilgeadam.mapper;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.UserResponseDto;
import com.bilgeadam.rabbitmq.model.RegisterMailModel;
import com.bilgeadam.repository.entity.UserProfile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserResponseDto fromDto(final UserSaveRequestDto dto);
    UserResponseDto toUserResponseDto(final UserProfile userProfile);

    UserResponseDto fromUserUpdateRequestDto(final UserUpdateRequestDto dto);
    UserProfile toUser (final UserUpdateRequestDto dto);
    UserProfile fromCreateUserRequestDto(final CreateUserRequestDto dto);
    UserProfile fromCreateAdminRequestDto(final CreateAdminRequestDto dto);

    UserProfile fromUpdateDtoToUserProfile(UserUpdateRequestDto dto);

    RegisterMailModel fromUserToRegisterModel(final UserProfile user);

}


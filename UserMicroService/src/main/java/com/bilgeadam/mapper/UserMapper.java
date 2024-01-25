package com.bilgeadam.mapper;

import com.bilgeadam.dto.request.AuthUpdateRequestDto;
import com.bilgeadam.dto.request.CreateUserRequestDto;
import com.bilgeadam.dto.request.UserSaveRequestDto;
import com.bilgeadam.dto.request.UserUpdateRequestDto;
import com.bilgeadam.dto.response.UserResponseDto;
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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserProfile fromUpdateDtoToUserProfile(UserUpdateRequestDto dto, @MappingTarget UserProfile user);

}


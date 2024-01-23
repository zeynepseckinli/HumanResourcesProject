package com.bilgeadam.mapper;

import com.bilgeadam.dto.request.UserSaveRequestDto;
import com.bilgeadam.dto.request.UserUpdateRequestDto;
import com.bilgeadam.dto.response.UserResponseDto;
import com.bilgeadam.repository.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserResponseDto fromDto(final UserSaveRequestDto dto);
    UserResponseDto toUserResponseDto(final UserProfile userProfile);
    UserResponseDto fromUserUpdateRequestDto(final UserUpdateRequestDto dto);
    UserProfile toUser (final UserUpdateRequestDto dto);

}


package com.bilgeadam.mapper;

import com.bilgeadam.dto.request.CreatePermissionRequestDto;
import com.bilgeadam.dto.response.PermissionListResponseDtoForRequestUser;
import com.bilgeadam.dto.response.PermissionListResponseDtoForResponseUser;
import com.bilgeadam.repository.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {

    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    Permission fromDto(final CreatePermissionRequestDto dto);
    PermissionListResponseDtoForResponseUser toDtoForResponseUser(final Permission permission);
    PermissionListResponseDtoForRequestUser toDtoForRequestUser(final Permission permission);


}
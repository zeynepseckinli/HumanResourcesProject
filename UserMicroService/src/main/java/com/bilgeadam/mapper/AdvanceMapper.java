package com.bilgeadam.mapper;

import com.bilgeadam.dto.request.CreateAdvanceRequestDto;
import com.bilgeadam.repository.entity.Advance;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdvanceMapper {

    AdvanceMapper INSTANCE = Mappers.getMapper(AdvanceMapper.class);

    Advance fromDto(final CreateAdvanceRequestDto dto);

}

package com.bilgeadam.mapper;


import com.bilgeadam.dto.request.CreateCompanyRequestDto;
import com.bilgeadam.dto.response.GetCompanyDetailsResponseDto;
import com.bilgeadam.repository.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyMapper {

    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    Company toCompany(final CreateCompanyRequestDto dto);

    GetCompanyDetailsResponseDto toCompanyResponseDto(final Company company);


}